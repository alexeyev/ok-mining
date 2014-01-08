package ru.stachek66.okminer.wiki.fetchers

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.wiki.WikiVisitor
import ru.stachek66.okminer.language.russian.Lexer
import ru.stachek66.okminer.wiki.utils.Helper

/**
 * @author alexeyev
 */
class LinksFetcher extends Fetcher[Iterable[String]] {
  private val log = LoggerFactory.getLogger("wiki-k-fetcher")

  override def fetch(handler: Iterable[String] => Unit) {
    new WikiVisitor().visit {
      page => {
        if (!page.isRedirect && !page.isDisambiguationPage && !page.isStub && !page.isSpecialPage) {
          handler(Helper.getAllFormsLinkSet(page.getWikiText))
        }
      }
    }
  }
}
