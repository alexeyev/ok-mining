package ru.stachek66.okminer.wiki.fetchers

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.Lexer
import ru.stachek66.okminer.wiki.utils.Helper
import ru.stachek66.okminer.wiki.WikiVisitor


/**
 * title, text, links
 */
class TextFetcher extends Fetcher[(String, String)] {
  private val log = LoggerFactory.getLogger("wiki-fetcher")

  override def fetch(handler: ((String, String)) => Unit) {
    new WikiVisitor().visit {
      page => {
        if (!page.isRedirect && !page.isDisambiguationPage && !page.isStub && !page.isSpecialPage) {
          val text =
            Lexer.split(
              page.getTitle.toLowerCase + " " + page.getText.toLowerCase).mkString(" ")
          handler(page.getTitle.trim, text)
        }
      }
    }
  }
}
