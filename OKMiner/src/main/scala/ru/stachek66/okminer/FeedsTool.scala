package ru.stachek66.okminer

import com.sun.syndication.feed.synd.SyndEntry
import com.sun.syndication.feed.synd.SyndFeed
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Date
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._
import ru.stachek66.okminer.ner.NaiveNER
import org.apache.commons.collections.bag.HashBag

case class Entity(uri: String, description: String, date: Date)

/**
 * Запуск цепочки обработки на фидах news.yandex.ru за сегодняшний день --
 * для отладки алгоритмов.
 * @author alexeyev
 */
object FeedsTool extends App {

  private val log = LoggerFactory.getLogger(getClass)

  private def toEntity(entry: SyndEntry) =
    Entity(entry.getUri, entry.getDescription.getValue, entry.getPublishedDate)

  def getCurrent(url: URL): Iterable[Entity] = {
    val httpcon = url.openConnection().asInstanceOf[HttpURLConnection]
    val input = new SyndFeedInput()
    val feed: SyndFeed = input.build(new XmlReader(httpcon))
    feed.getEntries().toList.map(e => toEntity(e.asInstanceOf[SyndEntry]))
  }

  val urls = Iterable(
    "http://news.yandex.ru/computers.rss",
    "http://news.yandex.ru/hardware.rss",
    "http://news.yandex.ru/software.rss",
    "http://news.yandex.ru/security.rss",
    "http://news.yandex.ru/science.rss",
    "http://news.yandex.ru/blogs.rss",
    "http://news.yandex.ru/internet.rss"
  )
  val uriSet = collection.mutable.HashSet[String]()

  val corpus = for {
    stringUrl <- urls
    url = new URL(stringUrl)
    entity <- getCurrent(url)
    if !uriSet.contains(entity.uri)
  } yield {
    uriSet += entity.uri
    entity
  }

  //-------------------------------------------------------

  val pairs: Iterable[(String, String)] = corpus.flatMap {
    case Entity(uri, description, date) => {
      val trends = TrendsTool.extractTrends(description)
      val companies = NaiveNER.extractAllCompanies(description)
      for {
        trend <- trends
        company <- companies
      } yield (trend, company)
    }
  }

  val counts =
    for ((company, pairColl) <- pairs.groupBy(_._2))
    yield {
      // никогда, никогда не делайте так
      val trends = pairColl.
        map(_._1).
        groupBy(t => t).
        map(t => (t._1, t._2.size)).
        toSeq.
        sortBy(-_._2)
      (company, trends)
    }

  for ((company, trends) <- counts) {
    println(company)
    for ((trend, count) <- trends) {
      println("    " + count + "\t" + trend)
    }
  }
}
