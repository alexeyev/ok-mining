package ru.stachek66.okminer.wiki.keyphrases

import java.util.Date
import org.apache.lucene.index.{Term, IndexReader}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search._
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.Meta
import ru.stachek66.okminer.language.russian.Tokenizer
import scala.util.{Success, Failure, Try}
import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import scala.util.Success
import scala.util.Failure

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
      log.info("Keyphrases index found. Version: " + r.getVersion)
      r
  }

  private val searcher = new IndexSearcher(reader, null)
  private val qp = new QueryParser(
    Meta.luceneVersion,
    IndexProperties.textField,
    IndexProperties.analyzer)

  def buildQuery(keyphrase: String) = {
    val pq = new PhraseQuery()
    val splitted = Tokenizer.tokenize(keyphrase)
    log.debug("Query: [%s]".format(splitted.mkString(" ")))
    for (t <- splitted) {
      pq.add(new Term(IndexProperties.textField, t))
    }
    pq.setSlop(0)
    pq
  }

  def getHitsCount(keyphrase: String): Int = {
    val pq = buildQuery(keyphrase)
    val collector = TopScoreDocCollector.create(5000, true)
    searcher.search(pq, collector)
    collector.topDocs().scoreDocs.length
  }

  def tryPhrase(keyphrase: String) = {
    val pq = buildQuery(keyphrase)
    val l = keyphrase.split(" ").length
    val collector = TopScoreDocCollector.create(500000, true)
    searcher.search(pq, collector)
    val res = collector.topDocs().
      scoreDocs.
      map(doc => (doc.score, searcher.doc(doc.doc).getField("text").stringValue())).
      filter(p => p._2.split(" ").length == l).
      toList
    log.info(keyphrase + " => " + res.toString())
    res.map(_._2)
  }
}
