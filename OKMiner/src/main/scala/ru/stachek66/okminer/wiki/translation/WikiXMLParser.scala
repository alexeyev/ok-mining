package ru.stachek66.okminer.wiki.translation

import ru.stachek66.okminer.wiki._
import scala.collection.mutable.ArrayBuffer

/**
 * @author alexeyev
 */
class WikiXMLParser(from: String, to: String) {

  lazy val links: Iterable[(Long, String, String)] = {
    val collector = ArrayBuffer[(Long, String, String)]()
    new WikiVisitor(dumps(from)).visit {
      page => {
        val trt = page.getTranslatedTitle(to)
        collector ++= (Option(trt) match {
          case Some(title) => Seq((page.getID.toLong, trt.trim, page.getTitle.trim))
          case None => Seq()
        })
      }
    }
    collector
  }
}
