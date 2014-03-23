package ru.stachek66.okminer.wiki.fetchers

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.wiki.utils.Helper


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
            "%s %s".format(page.getTitle.replaceAll("_", " "), page.getText)
          handler(page.getTitle.trim, text)
        }
      }
    }
  }
}
