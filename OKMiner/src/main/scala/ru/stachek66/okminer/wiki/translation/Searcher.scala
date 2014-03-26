package ru.stachek66.okminer.wiki.translation

import org.apache.lucene.index.IndexReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{TopScoreDocCollector, IndexSearcher}
import ru.stachek66.okminer.Meta


/**
 * Wikipedia-based translation.
 * @author alexeyev
 */
object Searcher {
  //(index: Index) {

  import IndexProperties._

  private val index = new Index()
  private val reader = IndexReader.open(index.index)
  private val searcher = new IndexSearcher(reader)
  private val qp = new QueryParser(Meta.luceneVersion, ru, index.analyzer)

  def search(freeTextQuery: String, threshold: Option[Float] = None): Iterable[(Float, RuEn)] = {
    val collector = TopScoreDocCollector.create(10000, true)
    if (freeTextQuery == null) {
      println("HELLO I AM NULL LOL")
    }
    searcher.search(qp.parse(freeTextQuery), collector)
    for {
      scoreDoc <- collector.topDocs().scoreDocs.toIterable
      if threshold.filter(_ > scoreDoc.score).isEmpty
      doc = searcher.doc(scoreDoc.doc)
    } yield (scoreDoc.score, (doc.getField(ru).stringValue(), doc.getField(en).stringValue()))
  }
}
