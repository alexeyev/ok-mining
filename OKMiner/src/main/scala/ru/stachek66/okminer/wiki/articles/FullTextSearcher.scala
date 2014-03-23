//package ru.stachek66.okminer.wiki.articles
//
//import org.apache.lucene.index.IndexReader
//import org.apache.lucene.queryparser.classic.QueryParser
//import org.apache.lucene.search._
//import org.slf4j.LoggerFactory
//import ru.stachek66.okminer.Meta
//import ru.stachek66.okminer.language.russian.Lexer
//import scala.util.Failure
//import scala.util.Success
//import scala.util.Try
//
///**
// * @author alexeyev
// */
//class FullTextSearcher {
//
//  private val log = LoggerFactory.getLogger("wiki-text-searcher")
//
//  private val reader = Try {
//    IndexReader.open(IndexProperties.index.index)
//  } match {
//    case Failure(f) =>
//      log.error("No index found", f)
//      log.error("Exiting.")
//      throw new Error()
//    case Success(r) =>
//      log.info("Index found. v: " + r.getVersion)
//      r
//  }
//
//  private val searcher = new IndexSearcher(reader)
//  private val qp = new QueryParser(
//    Meta.luceneVersion, IndexProperties.textField, IndexProperties.analyzer)
//
//  BooleanQuery.setMaxClauseCount(5000)
//
//  private def buildQuery(rawQuery: String): Query = {
//    val splitted = Lexer.split(rawQuery) //Tokenizer.tokenize(rawQuery)
//    val query = qp.parse(splitted.mkString(" "))
//    log.debug("Query: [%s]".format(query))
//    query
//  }
//
//  def getHitsCount(q: String): Int = {
//    val pq = buildQuery(q)
//    val collector = TopScoreDocCollector.create(5000, true)
//    searcher.search(pq, collector)
//    collector.topDocs().scoreDocs.length
//  }
//
//  def tryQuery(q: String, k: Int) = {
//    val pq = buildQuery(q)
//    log.info(pq.toString)
//    val collector = TopScoreDocCollector.create(500000, true)
//    searcher.search(pq, collector)
//    val res =
//      collector.topDocs().
//        scoreDocs.take(k).
//        map(
//        doc =>
//          (doc.score,
//            searcher.doc(doc.doc).getField(IndexProperties.titleField).stringValue())).
//        toList
//    log.info(res.mkString("\n"))
//    res.map(_._2)
//  }
//}
