package ru.stachek66.okminer.wiki.articles

import org.apache.lucene.index.IndexReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{TopScoreDocCollector, IndexSearcher}
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.Meta
import scala.util.{Success, Try, Failure}

/**
 * Script searching for keyphrases in the whole text collection.
 * @author alexeyev
 */
object Searcher {

  private val log = LoggerFactory.getLogger("wiki-text-searcher")

  private lazy val reader = Try {
    IndexReader.open(IndexProperties.index)
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

  //  println("Number of docs in index : " + r eader.maxDoc())
  //  val t = new Term("text", "королевство")
  //  val df = reader.docFreq(t)
  //  println("DF = " + df)


  private val searcher = new IndexSearcher(reader)
  private val qp = new QueryParser(Meta.luceneVersion, IndexProperties.textField, IndexProperties.analyzer)

  def getHitsCount(keyphrase: String): Int = {
    val collector = TopScoreDocCollector.create(500000, true)
    searcher.search(qp.parse(keyphrase), collector)
    collector.topDocs().
      scoreDocs.length
    //        map(doc => searcher.doc(doc.doc).
    //        getField("text")).toList
  }

  def main(args: Array[String]) {
    println(getHitsCount("королевство польское"))
  }
}

