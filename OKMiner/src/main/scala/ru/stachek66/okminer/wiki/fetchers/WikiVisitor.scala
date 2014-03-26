package ru.stachek66.okminer.wiki.fetchers

import edu.jhu.nlp.wikipedia.{WikiPage, PageCallbackHandler, WikiXMLParserFactory}
import java.io.File
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.utils.CounterLogger
import ru.stachek66.okminer.wiki._

/**
 * A wrapper over a lightweight wiki-parser for managing WikiPages.
 * @author alexeyev
 */
class WikiVisitor(file: File, clog: CounterLogger) {

  def this() = this(
    dumps("ru"),
    new CounterLogger(LoggerFactory.getLogger("wiki-visitor"), 1000, "%s articles processed"))

  def this(file: File) = this()

  def this(clog: CounterLogger) = this(dumps("ru"), clog)

  private val wxsp = WikiXMLParserFactory.getSAXParser(file.getAbsolutePath)

  def visit(handler: WikiPage => Unit) {
    try {
      wxsp.setPageCallback(
        new PageCallbackHandler {
          def process(page: WikiPage) {
            clog.execute {
              handler(page)
            }
          }
        }
      )
      wxsp.parse()
    }
  }
}