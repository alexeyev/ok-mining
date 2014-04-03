package ru.stachek66.okminer.wiki.fetchers

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.wiki.utils.Helper
import ru.stachek66.okminer.utils.CounterLogger

/**
 * Fetching all textual links
 * @author alexeyev
 */
class LinksFetcher extends Fetcher[Iterable[String]] {
  private val log = LoggerFactory.getLogger("wiki-k-fetcher")

  /**
   * Allows to do anything with the text link or title
   * @param handler function processing the given link
   */
  override def fetch(handler: Iterable[String] => Unit) {
    new WikiVisitor(new CounterLogger(log, 1000, "%s links got")).visit {
      page => {
        if (Helper.isCoolPage(page)) {
          handler(Helper.getTitleFormLinkSet(page.getWikiText))
        }
      }
    }
  }
}
