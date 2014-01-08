package ru.stachek66.okminer.wiki

import edu.jhu.nlp.wikipedia.{WikiPage, PageCallbackHandler, WikiXMLParserFactory}
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.utils.CounterLogger

/**
 * @author alexeyev
 */
class WikiVisitor(clog: CounterLogger) {

  def this() = this(new CounterLogger(LoggerFactory.getLogger("wiki-visitor"), 1000, "%s articles processed"))

  val wxsp = WikiXMLParserFactory.getSAXParser(dump.getAbsolutePath)

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