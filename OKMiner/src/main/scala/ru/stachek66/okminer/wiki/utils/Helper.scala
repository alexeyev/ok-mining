package ru.stachek66.okminer.wiki.utils

import java.util.regex.Pattern
import scala.collection.mutable.ArrayBuffer
import edu.jhu.nlp.wikipedia.WikiPage

/**
 * Stuff for wiki markup processing.
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

  def getTitleFormLinkSet(text: String): Iterable[String] = {
    val pipeLinks = getPipeLinksMap(text)
    (
      getNonPipeLinks(text) ++
        pipeLinks.values
      ).toSet
  }

  def getAllFormsLinkSet(text: String): Iterable[String] = {
    val pipeLinks = getPipeLinksMap(text)
    (
      getNonPipeLinks(text) ++
        pipeLinks.values ++
        pipeLinks.keys
      ).toSet
  }

  def isCoolPage(page: WikiPage) = !page.isRedirect && !page.isSpecialPage && !page.isDisambiguationPage && !page.isStub
}
