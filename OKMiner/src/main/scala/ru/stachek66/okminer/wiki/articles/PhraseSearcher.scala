package ru.stachek66.okminer.wiki.articles

import org.apache.lucene.index.{Term, IndexReader}
import org.apache.lucene.search.{Query, PhraseQuery, TopScoreDocCollector, IndexSearcher}
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.Lexer
import scala.util.{Success, Try, Failure}

/**
 * Searching for phrases in the whole wiki-articles' collection
 * @author alexeyev
 */
class PhraseSearcher(index: Index) {

  private val log = LoggerFactory.getLogger("wiki-text-searcher")

  private val reader = Try {
    IndexReader.open(index.accessIndex)
  } match {
    case Failure(f) =>
      log.error("No index found", f)
      throw new Error("Problem officer")
    case Success(r) =>
      log.info("Index found. v: " + r.getVersion)
      r
  }

  private val searcher = new IndexSearcher(reader)

  /**
   * A total number of indexed wiki-articles
   */
  val totalDocs = searcher.collectionStatistics(IndexProperties.textField).docCount()

  private def buildQuery(keyphrase: String): Query = {
    val pq = new PhraseQuery()
    val splitted = Lexer.split(keyphrase)
    log.debug("Query: [%s]".format(splitted.mkString(" ")))
    for (t <- splitted) {
      pq.add(new Term(IndexProperties.textField, t))
    }
    pq.setSlop(0)
    pq
  }

  /**
   * Returns a number of times a keyphrase was found in the index
   */
  def getHitsCount(keyphrase: String): Int = {
    val pq = buildQuery(keyphrase)
    val collector = TopScoreDocCollector.create(500, true)
    searcher.search(pq, collector)
    collector.topDocs().scoreDocs.length
  }

  /**
   * Returns the result of searching the given keyphrase
   */
  def tryPhrase(keyphrase: String) = {
    val pq = buildQuery(keyphrase)
    val collector = TopScoreDocCollector.create(500000, true)
    searcher.search(pq, collector)
    collector.topDocs().
      scoreDocs.toIterable.
      map(doc => searcher.doc(doc.doc).getField(IndexProperties.textField).stringValue()).
      toList
  }
}

