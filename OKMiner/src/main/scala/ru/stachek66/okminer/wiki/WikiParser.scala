package ru.stachek66.okminer.wiki

import edu.jhu.nlp.wikipedia.{WikiPage, PageCallbackHandler, WikiXMLParserFactory}
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._
import java.io.FileWriter
import ru.stachek66.okminer.utils.CounterLogger

/**
 * @author alexeyev
 */
object WikiParser extends App {
  private val log = LoggerFactory.getLogger(this.getClass)
  private val clog = new CounterLogger(log, 1000, "%s articles processed")

  val wxsp = WikiXMLParserFactory.getSAXParser(
    "../ruwiki-latest-pages-articles-multistream.xml")
  val writer = new FileWriter("wiki-links-russian-from-parser.txt")

  try {
    wxsp.setPageCallback(
      new PageCallbackHandler {
        def process(page: WikiPage) {
          clog.tick()
          if (page.getLinks.nonEmpty)
                                                         //          if (
          // page.getTranslatedTitle("en") != null)
          //            log.info(" " + page.getTranslatedTitle("en") +" " + page.getID + " " + page.getLinks)
            writer.write("%s;%s\n".format(page.getTitle.trim, page.getLinks.map("\"%s\"".format(_)).mkString(",")))

        }
      }
    )
    wxsp.parse()
  } catch {
    case e: Exception =>
      log.error("Oops", e)
  }
  writer.close()
}
