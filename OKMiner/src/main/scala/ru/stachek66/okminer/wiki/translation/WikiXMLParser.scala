package ru.stachek66.okminer.wiki.translation

import ru.stachek66.okminer.wiki._
import scala.collection.mutable.ArrayBuffer

/**
 * @author alexeyev
 */
class WikiXMLParser(langCode: String) {

  lazy val links: Iterable[(Long, String, String)] = {
    val collector = ArrayBuffer[(Long, String, String)]()
    new WikiVisitor(dumps("ru")).visit {
      page => {
        val trt = page.getTranslatedTitle("en")
        collector ++= (Option(trt) match {
          case Some(title) => Seq((page.getID.toLong, trt.trim, page.getTitle.trim))
          case None => Seq()
        })
      }
    }
    collector
  }
}
