package ru.stachek66.okminer.wiki.fetchers

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.Lexer
import ru.stachek66.okminer.wiki.utils.Helper
import ru.stachek66.okminer.wiki.WikiVisitor


/**
 * title, text
 */
class TextFetcher extends Fetcher[(String, String)] {
  private val log = LoggerFactory.getLogger("wiki-text-fetcher")

  override def fetch(handler: ((String, String)) => Unit) {
    new WikiVisitor().visit {
      page => {
        if (Helper.isCoolPage(page)) {
          val text =
            Lexer.split(
              page.getTitle.toLowerCase.replaceAll("_", " ") + " " + page.getText.toLowerCase).mkString(" ")
          handler(page.getTitle.trim, text)
        }
      }
    }
  }
}
