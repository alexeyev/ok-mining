package ru.stachek66.okminer.wiki

import edu.jhu.nlp.wikipedia.{WikiPage, PageCallbackHandler, WikiXMLParserFactory}
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.utils.CounterLogger
import java.io.File

/**
 * @author alexeyev
 */
class WikiVisitor(file: File, clog: CounterLogger) {

  def this() = this(
    dumps("ru"),
    new CounterLogger(LoggerFactory.getLogger("wiki-visitor"), 1000, "%s articles processed"))

  def this(file:File) = this()

  val wxsp = WikiXMLParserFactory.getSAXParser(file.getAbsolutePath)

  def visit(handler: WikiPage => Unit) {
    try {
      wxsp.setPageCallback(
        new PageCallbackHandler {
          def process(page: WikiPage) {
            clog.tick()
            handler(page)
          }
        }
      )
      wxsp.parse()
    }
  }
}