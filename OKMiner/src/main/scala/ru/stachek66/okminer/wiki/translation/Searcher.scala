package ru.stachek66.okminer.wiki.translation

import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.TopScoreDocCollector
import ru.stachek66.okminer.Meta
import org.slf4j.LoggerFactory


/**
 * Wikipedia-based translation.
 * @author alexeyev
 */
object Searcher {

  import IndexProperties._

  private val index = new Index()
  private val qp = new QueryParser(Meta.luceneVersion, ru, index.analyzer)
  private val log = LoggerFactory.getLogger("translator-searcher")

  def search(freeTextQuery: String, threshold: Option[Float] = None): Iterable[(Float, RuEn)] = {
    val collector = TopScoreDocCollector.create(10000, true)

    def splSize(s: String) = s.split("\\s").size

    val querySize = splSize(freeTextQuery)

    index.withSearcher {
      searcher => {
        try {
          val q = qp.createMinShouldMatchQuery(IndexProperties.ru, freeTextQuery, 1.0f)
          println(q, freeTextQuery, qp, searcher)
          searcher.search(q, collector)
          for {
            scoreDoc <- collector.topDocs().scoreDocs.toIterable
            if threshold.filter(_ > scoreDoc.score).isEmpty
            doc = searcher.doc(scoreDoc.doc)
            if splSize(doc.get(IndexProperties.ru)) == querySize
          } yield (scoreDoc.score, (doc.getField(ru).stringValue(), doc.getField(en).stringValue()))
        } catch {
          case e: NullPointerException =>
            log.error(s"Problems while searching [$freeTextQuery]", e)
            Iterable.empty
        }
      }
    }
  }
}
