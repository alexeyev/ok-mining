package ru.stachek66.okminer.wiki.keyphrases

import org.apache.lucene.index.{Term, IndexReader}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{PhraseQuery, TopScoreDocCollector, IndexSearcher}
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.Meta
import scala.util.{Success, Failure, Try}
import ru.stachek66.okminer.language.russian.{Tokenizer, Stemmer, Lexer}

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

  private val searcher = new IndexSearcher(reader, null)
  private val qp = new QueryParser(
    Meta.luceneVersion,
    IndexProperties.textField,
    IndexProperties.analyzer)


  def getHitsCount(keyphrase: String): Int = {
    val collector = TopScoreDocCollector.create(500000, true)
    searcher.search(qp.parse(keyphrase), collector)
    val hits = collector.topDocs().scoreDocs
    //    log.info(hits.map(d => searcher.doc(d.doc)).toList.mkString("\n"))
    hits.length
  }

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
    println(getHitsCount("польское королевство"))
  }
}
