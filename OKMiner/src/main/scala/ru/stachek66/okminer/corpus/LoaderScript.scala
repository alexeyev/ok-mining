package ru.stachek66.okminer.corpus

import java.io.IOException
import java.net.URL
import ru.stachek66.okminer.cleaning.StructuredExtractor

/**
 * @author alexeyev
 */
object LoaderScript {

  def main(args: Array[String]) {
    val links: Array[String] = for (link <- links) {
      try {
        val se: StructuredExtractor = new StructuredExtractor(new URL(link))
        p(se.getLinksFromPosts)
      }
      catch {
        case e: IOException => {
          p("problem")
        }
      }
    }
  }

  lazy val links =
    Array("http://odnoklassniki.ru/poln.hohot",
      "http://odnoklassniki.ru/group/44732901687435",
      "http://odnoklassniki.ru/beeline.kazakhstan",
      "http://odnoklassniki.ru/academy",
      "http://odnoklassniki.ru/samsung",
      "http://odnoklassniki.ru/ochenpros",
      "http://odnoklassniki.ru/fczenit")
}
