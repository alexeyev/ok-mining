package ru.stachek66.okminer.ner.indexing

import org.apache.lucene.document.Document
import org.apache.lucene.index._
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.sandbox.queries.SlowFuzzyQuery
import org.apache.lucene.search._
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.Meta
import ru.stachek66.okminer.language.russian.Tokenizer

/**
 * Searching companies' names.
 * Is a singleton due to holding the index.
 * @author alexeyev
 */
class Searcher {

  private val index = new RAMIndex()
  private val log = LoggerFactory.getLogger("company-searcher-experimental")

  /**
   * "Fuzzy search", which means Levenstein distance search.
   * @param freeTextQuery query in natural Russian language
   * @param maxEditDistance max allowable eedit distance
   * @return pairs: (distance, company-document)
   */
  @deprecated
  def fuzzyFind(freeTextQuery: String, maxEditDistance: Int): Iterable[(Float, Document)] = {

    /*
     * Sadly, one can't use FuzzyQuery: max edit distance = 2
     * UNACCEPTABLE!
     *
     * CITING:
     * * @param minimumSimilarity a value between 0 and 1 to set the required similarity
     *  between the query term and the matching terms. For example, for a
     *  <code>minimumSimilarity</code> of <code>0.5</code> a term of the same length
     *  as the query term is considered similar to the query term if the edit distance
     *  between both terms is less than <code>length(term)*0.5</code>
     *  <p>
     *  Alternatively, if <code>minimumSimilarity</code> is >= 1f, it is interpreted
     *  as a pure Levenshtein edit distance. For example, a value of <code>2f</code>
     *  will match all terms within an edit distance of <code>2</code> from the
     *  query term. Edit distances specified in this way may not be fractional.
     */
    val fq =
      new SlowFuzzyQuery(new Term(IndexProperties.companyField, freeTextQuery), maxEditDistance)

    index.withSearcher {
      searcher => {
        val collector = TopScoreDocCollector.create(10, true)
        searcher.search(fq, collector)
        val hits =
          collector.topDocs().
            scoreDocs.toIterable.
            map(
            document =>
              (document.score, searcher.doc(document.doc))
          )
        hits.toIterable
      }
    }
  }

  /**
   * Default vector-model Lucene search.
   * @param freeTextQuery request in Russian language
   * @param relevanceThreshold tf-idf-like measure threshold
   * @return a list of pairs (score, document)
   */
  @deprecated // since March; should use strict match
  def magicFind(freeTextQuery: String, relevanceThreshold: Float): Iterable[(Float, Document)] = {

    index.withSearcher {
      searcher => {

        val qp = new QueryParser(Meta.luceneVersion, IndexProperties.companyField, index.analyzer)

        val collector = TopScoreDocCollector.create(1000, true)
        searcher.search(qp.parse(freeTextQuery), collector)
        val hits =
          collector.topDocs().
            scoreDocs.toIterable.
            map(
            document =>
              (document.score, searcher.doc(document.doc))
          ).filter(
            _._1 > relevanceThreshold
          )
        if (!hits.isEmpty)
          log.debug(hits.toIterable.toString())
        hits.toIterable
      }
    }
  }

  /**
   * Exact match queries.
   * @param freeTextQuery company name to be searched for
   * @return matched companies
   */
  def strictFind(freeTextQuery: String): Iterable[(Float, Document)] = {

    index.withSearcher {
      searcher => {
        val qp = new QueryParser(Meta.luceneVersion, IndexProperties.companyField, index.analyzer)
        val q = qp.createMinShouldMatchQuery(IndexProperties.companyField, freeTextQuery, 1.0f)
        val collector = TopScoreDocCollector.create(1000, true)
        if (q == null) {
          log.error(s"Query is null for freetext [$freeTextQuery]")
          Iterable.empty
        } else {
          searcher.search(q, collector)
          val hits =
            collector.topDocs().
              scoreDocs.toIterable.
              map(
              document => {
                (document.score, searcher.doc(document.doc))
              }
            ).filter {
              case (score, doc) => {
                doc.
                  getValues(IndexProperties.companyField).
                  head.split("[\\s'-\\.,:;!\\?]").
                  size == Tokenizer.tokenize(freeTextQuery).size
              }
            }
          if (!hits.isEmpty)
            log.debug(hits.toIterable.toString())
          hits.toIterable
        }
      }
    }
  }

}
