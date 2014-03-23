package ru.stachek66.okminer.wiki.fetchers

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.wiki.utils.Helper

/**
 * Fetching all textual links
 * @author alexeyev
 */
class LinksFetcher extends Fetcher[Iterable[String]] {
  private val log = LoggerFactory.getLogger("wiki-k-fetcher")

  override def fetch(handler: Iterable[String] => Unit) {
    new WikiVisitor().visit {
      page => {
        if (Helper.isCoolPage(page)) {
          handler(Helper.getTitleFormLinkSet(page.getWikiText))
        }
      }
    }
  }
}
