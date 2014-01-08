package ru.stachek66.okminer.wiki.utils

import java.util.regex.Pattern
import scala.collection.mutable.ArrayBuffer

/**
 * @author alexeyev
 */
object Helper {
  private val pipeLinkPattern = Pattern.compile("\\[\\[([^:\\]]+)\\|([^\\]]+)\\]\\]")
  private val linkPattern = Pattern.compile("\\[\\[([^:\\]\\|]+)\\]\\]")

  /**
   * Surface form to normal form.
   */
  def getPipeLinksMap(text: String): Map[String, String] = {
    val m = pipeLinkPattern.matcher(text)
    val list = ArrayBuffer[(String, String)]()
    while (m.find()) {
      list += ((m.group(2), m.group(1)))
    }
    list.toMap
  }

  private def getNonPipeLinks(text: String): Iterable[String] = {
    val m = linkPattern.matcher(text)
    val list = ArrayBuffer[String]()
    while (m.find()) {
      list += m.group(1)
    }
    list.toList
  }

  def getLinkSet(text: String): Iterable[String] = {
    val pipeLinks = getPipeLinksMap(text)
    (
      getNonPipeLinks(text) ++
        pipeLinks.values
      ).toSet
  }
}
