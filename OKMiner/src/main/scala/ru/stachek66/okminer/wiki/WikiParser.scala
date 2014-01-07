package ru.stachek66.okminer.wiki

import edu.jhu.nlp.wikipedia.{WikiPage, PageCallbackHandler, WikiXMLParserFactory}
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._

/**
 * @author alexeyev
 */
object WikiParser extends App {
  private val log = LoggerFactory.getLogger(this.getClass)
  val wxsp = WikiXMLParserFactory.getSAXParser(
    "../ruwiki-latest-pages-articles-multistream.xml")

  try {
    wxsp.setPageCallback(
      new PageCallbackHandler {
        def process(page: WikiPage) {
          if (page.getTranslatedTitle("en") != null)
            log.info(" " + page.getTranslatedTitle("en") +" " + page.getID + " " + page.getLinks)
        }
      }
    )
    wxsp.parse()
  } catch {
    case e: Exception =>
      log.error("Oops", e)
  }
}
