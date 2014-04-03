package ru.stachek66.okminer.wiki.fetchers

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.wiki.utils.Helper
import ru.stachek66.okminer.utils.CounterLogger


/**
 * Fetcher allowing to process all wiki-articles and all wiki-titles.
 */
class TextFetcher extends Fetcher[(String, String)] {
  private val log = LoggerFactory.getLogger("wiki-text-fetcher")

  /**
   * @param handler processor of the pair (wiki-article title, wiki-article text)
   */
  override def fetch(handler: ((String, String)) => Unit) {
    new WikiVisitor(new CounterLogger(log, 1000, "%s texts got")).visit {
      page => {
        if (Helper.isCoolPage(page)) {
          val text =
            "%s %s".format(page.getTitle.replaceAll("_", " "), page.getText)
          handler(page.getTitle.trim, text)
        }
      }
    }
  }
}
