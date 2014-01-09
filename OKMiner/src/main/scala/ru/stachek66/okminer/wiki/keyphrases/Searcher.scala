package ru.stachek66.okminer.wiki.keyphrases

import org.apache.lucene.index.IndexReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{TopScoreDocCollector, IndexSearcher}
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.Meta
import scala.util.{Success, Failure, Try}

/**
 * @author alexeyev
 */
object Searcher {
  //todo:

  private val log = LoggerFactory.getLogger("wiki-links-searcher")

  private lazy val reader = Try {
    IndexReader.open(IndexProperties.index)
    //    throw new Exception
  } match {
    case Failure(f) =>
      log.error("No index found", f)
      IndexProperties.index.getDirectory.mkdirs()
      log.info("Deleting old index: " + IndexProperties.index.getDirectory.listFiles().foreach(_.delete()))
      new Indexer().doIndex()
      IndexReader.open(IndexProperties.index)
    case Success(r) =>
      r
  }

  private val searcher = new IndexSearcher(reader)
  private val qp = new QueryParser(Meta.luceneVersion, IndexProperties.textField, IndexProperties.analyzer)

  def getHitsCount(keyphrase: String): Int = {
    val collector = TopScoreDocCollector.create(500000, true)
    searcher.search(qp.parse(keyphrase), collector)
    val hits = collector.topDocs().scoreDocs
//    log.info(hits.map(d => searcher.doc(d.doc)).toList.mkString("\n"))
    hits.length
  }

  def main(args: Array[String]) {
    println(getHitsCount("польское королевство"))
  }
}
