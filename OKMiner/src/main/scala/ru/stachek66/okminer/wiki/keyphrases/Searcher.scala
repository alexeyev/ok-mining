package ru.stachek66.okminer.wiki.keyphrases

import org.apache.lucene.index.IndexReader
import org.apache.lucene.search._
import org.slf4j.LoggerFactory
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import org.apache.lucene.queryparser.classic.QueryParser
import ru.stachek66.okminer.Meta

/**
 * Wiki links and titles search provider.
 * @author alexeyev
 */
class Searcher(index: Index) {

  private val log = LoggerFactory.getLogger("wiki-links-searcher")

  private val reader = Try {
    IndexReader.open(index.accessIndex)
  } match {
    case Failure(f) =>
      log.error("No index found, bye", f)
      throw new Error()
    case Success(r) =>
      log.info("Keyphrases index found. Version: " + r.getVersion)
      r
  }

  private val searcher = new IndexSearcher(reader, null)

  val totalDocs = searcher.collectionStatistics(IndexProperties.textField).docCount()

  private def buildQuery(keyphrase: String) = {
    val qp = new QueryParser(Meta.luceneVersion, keyphrase, IndexProperties.analyzer)
    qp.setPhraseSlop(0)
    qp.createMinShouldMatchQuery(IndexProperties.textField, keyphrase, 1.0f)
    //    log.debug("Query: [%s]".format(splitted.mkString(" ")))

  }

  def getHitsCount(keyphrase: String): Int = {
    val pq = buildQuery(keyphrase)
    val collector = TopScoreDocCollector.create(5000, true)
    searcher.search(pq, collector)
    collector.topDocs().scoreDocs.length
  }

  private def tryPhrase(keyphrase: String): Iterable[String] = {
    val pq = buildQuery(keyphrase)
    val l = keyphrase.split(" ").length
    val collector = TopScoreDocCollector.create(500000, true)
    searcher.search(pq, collector)
    val res = for {
      scoreDoc <- collector.topDocs().scoreDocs.toIterable
      doc = searcher.doc(scoreDoc.doc)
      link = doc.getField(IndexProperties.textField).stringValue()
      // for exact match
      if link.split(" ").length == l
    } yield {
      (scoreDoc.score, link)
    }
    log.info(keyphrase + " => " + res.toString())
    res.map(_._2)
  }
}

object Searcher {
  def main(args: Array[String]) {
    val s = new Searcher(new Index())
    println(s.tryPhrase("облачное хранилище"))
    println(s.tryPhrase("облачное вычисления"))
    println(s.tryPhrase("интернет вещица"))
    println(s.tryPhrase("интернет вещей"))
    println(s.tryPhrase("смартфон "))
  }
}
