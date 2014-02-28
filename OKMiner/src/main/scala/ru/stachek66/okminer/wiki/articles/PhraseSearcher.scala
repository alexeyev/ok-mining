package ru.stachek66.okminer.wiki.articles

import org.apache.lucene.index.{Term, IndexReader}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{Query, PhraseQuery, TopScoreDocCollector, IndexSearcher}
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.Meta
import ru.stachek66.okminer.language.russian.Tokenizer
import scala.util.{Success, Try, Failure}

/**
 * Script searching for keyphrases in the whole text collection.
 * @author alexeyev
 */
class PhraseSearcher {

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
      log.info("Index found. v: " + r.getVersion)
      r
  }

  private val searcher = new IndexSearcher(reader)

  private def buildQuery(keyphrase: String): Query = {
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
    val collector = TopScoreDocCollector.create(500, true)
    searcher.search(pq, collector)
    collector.topDocs().scoreDocs.length
  }

  def tryPhrase(keyphrase: String) = {
    val pq = buildQuery(keyphrase)
    val collector = TopScoreDocCollector.create(500000, true)
    searcher.search(pq, collector)
    collector.topDocs().
      scoreDocs.
      map(doc => searcher.doc(doc.doc).getField("text").stringValue()).
      toList
  }
}
