package ru.stachek66.okminer.wiki.translation

import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.TopScoreDocCollector
import ru.stachek66.okminer.Meta
import org.slf4j.LoggerFactory


/**
 * Wikipedia-based translation
 * @author alexeyev
 */
object Searcher {

  import IndexProperties._

  private val index = new Index()
  private val qp = new QueryParser(Meta.luceneVersion, ru, index.analyzer)
  private val log = LoggerFactory.getLogger("translator-searcher")

  /**
   * Given a free text query (supposedly Russian article candidate name),
   * and a possible score threshold (not supported since april), returns a tuple:
   * (score, (matched Russian title, English title of the matched))
   * @param freeTextQuery article-name query in Russian
   * @param threshold  Lucene score filter, not used since April 2014
   * @return a collection of results: (score, (matched Russian title, English title of the matched))
   */
  def search(freeTextQuery: String, threshold: Option[Float] = None): Iterable[(Float, RuEn)] = {

    val collector = TopScoreDocCollector.create(10000, true)

    def splSize(s: String) = s.split(" ").size

    val querySize = splSize(freeTextQuery)

    index.withSearcher {
      searcher => {
        try {
          val q = qp.createMinShouldMatchQuery(IndexProperties.ru, freeTextQuery, 1.0f)
          searcher.search(q, collector)

          for {
            scoreDoc <- collector.topDocs().scoreDocs.toIterable
            //            if threshold.filter(_ > scoreDoc.score).isEmpty
            doc = searcher.doc(scoreDoc.doc)
            distance = splSize(doc.get(IndexProperties.ru)) - querySize
            if distance >= 0 && distance < 1
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
