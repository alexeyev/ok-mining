package ru.stachek66.okminer.ner

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.{Field, TextField, Document}
import org.apache.lucene.index._
import org.apache.lucene.sandbox.queries.SlowFuzzyQuery
import org.apache.lucene.search.{TopScoreDocCollector, IndexSearcher}
import org.apache.lucene.store.RAMDirectory
import ru.stachek66.okminer.Meta
import java.io.File
import org.apache.lucene.queryparser.classic.QueryParser
import org.slf4j.LoggerFactory

/**
 * Searching companies' names.
 * Is a singleton due to holding the index.
 * @author alexeyev
 */
object Searcher {

  val companyField = "name"

  private def addToIndex(iw: IndexWriter, company: String) {
    val doc = new Document()
    doc.add(new TextField(companyField, company, Field.Store.YES))
    iw.addDocument(doc)
  }

  private val analyzer: Analyzer = new StandardAnalyzer(Meta.luceneVersion)
  private val index = new RAMDirectory()
  private val config = new IndexWriterConfig(Meta.luceneVersion, analyzer)
  private val log = LoggerFactory.getLogger("company-searcher-experimental")

  private def fillIndex(sources: Iterable[File]) {
    log.info("Filling companies' index...")
    val iw = new IndexWriter(index, config)
    for (file <- sources) {
      io.Source.fromFile(file).getLines().
        foreach {
        line =>
          addToIndex(iw, line.trim)
      }
    }
    iw.close()
    log.info("Done.")
  }

  fillIndex(Iterable(new File("habrahabr-companies.tsv"), new File("crunchbase-companies.tsv")))


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
      new SlowFuzzyQuery(
        new Term(companyField, freeTextQuery),
        maxEditDistance)

    val reader = IndexReader.open(index)
    val searcher = new IndexSearcher(reader)

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

  /**
   * Exact match
   */
  def magicFind(freeTextQuery: String, relevanceThreshold: Float): Iterable[(Float, Document)] = {

    val reader = IndexReader.open(index)
    val searcher = new IndexSearcher(reader)
    val qp = new QueryParser(Meta.luceneVersion, companyField, analyzer)

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
