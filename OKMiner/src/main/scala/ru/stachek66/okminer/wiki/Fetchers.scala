package ru.stachek66.okminer.wiki

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.Lexer
import ru.stachek66.okminer.wiki.utils.Helper

/**
 * @author alexeyev
 */
trait Fetcher[T] {
  def fetch(handler: T => Unit): Unit
}

/**
 * title, text, links
 */
class WikiTextAndLinkFetcher extends Fetcher[(String, String, Iterable[String])] {
  private val log = LoggerFactory.getLogger("wiki-fetcher")

  override def fetch(handler: ((String, String, Iterable[String])) => Unit) {
    new WikiVisitor().visit {
      page => {
        val text =
          Lexer.split(
            page.getTitle.toLowerCase + " " + page.getText.toLowerCase).mkString(" ")
        handler(page.getTitle.trim, text, Helper.getLinkSet(page.getWikiText.toLowerCase()))
      }
    }
  }
}
