package ru.stachek66.okminer.wiki.articles

import org.apache.lucene.index.{Term, IndexReader}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{PhraseQuery, TopScoreDocCollector, IndexSearcher}
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.Meta
import scala.util.{Success, Try, Failure}
import ru.stachek66.okminer.wiki.keyphrases.{IndexProperties, Indexer}
import java.util.Date
import ru.stachek66.okminer.language.russian.Tokenizer
import ru.stachek66.okminer.wiki.articles.IndexProperties

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
      log.info("Index found. v: " + new Date(r.getVersion))
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

  }
//
//  def getResultsAsStrings(keyphrase: String) = {
//    val collector =
//      TopScoreDocCollector.create(500000, true)
//    searcher.search(qp.parse(keyphrase), collector)
//    collector.topDocs().
//      scoreDocs.
//      map(doc => searcher.doc(doc.doc).getField("text").stringValue()).
//      toList
//  }


  def tryPhrase(keyphrase: String) = {

    val pq = new PhraseQuery()
    val splitted = Tokenizer.tokenize(keyphrase)
    log.info("Query: [%s]".format(splitted.mkString(" ")))
    for (t <- splitted) {
      pq.add(new Term(IndexProperties.textField, t))
    }
    pq.setSlop(2)

    val collector = TopScoreDocCollector.create(500000, true)
    searcher.search(pq, collector)

    collector.topDocs().
      scoreDocs.
      map(doc => searcher.doc(doc.doc).getField("text").stringValue()).
      toList
  }

  def main(args: Array[String]) {
    println(getHitsCount("королевство польское"))
  }
}

