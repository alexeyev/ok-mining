package ru.stachek66.okminer.wiki

import org.apache.lucene.index.IndexReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{TopScoreDocCollector, IndexSearcher}
import ru.stachek66.okminer.Meta

/**
 * @author alexeyev
 */
object Searcher extends App {

  new WikiIndexer().doIndex()

  val reader = IndexReader.open(IndexProperties.index)
  val searcher = new IndexSearcher(reader)
  val qp = new QueryParser(Meta.luceneVersion, "text", IndexProperties.analyzer)

  val collector = TopScoreDocCollector.create(10, true)
  searcher.search(qp.parse("компьютерная лингвистика"), collector)

  val hits =
    collector.topDocs().
      scoreDocs.toIterable.
      map(
      document =>
        (document.score, searcher.doc(document.doc))
    )
  println(hits.toString())
}

