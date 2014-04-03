package ru.stachek66.okminer.wiki.utils

import java.util.regex.Pattern
import scala.collection.mutable.ArrayBuffer
import edu.jhu.nlp.wikipedia.WikiPage

/**
 * Tools for wiki markup processing.
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

  /**
   * Getting links without pipes.
   * E.g.: [ [ Красота ] ]
   */
  private def getNonPipeLinks(text: String): Iterable[String] = {
    val m = linkPattern.matcher(text)
    val list = ArrayBuffer[String]()
    while (m.find()) {
      list += m.group(1)
    }
    list.toList
  }

  /**
   * Normal link forms
   * @param text irredular form
   */
  def getTitleFormLinkSet(text: String): Iterable[String] = {
    val pipeLinks = getPipeLinksMap(text)
    (
      getNonPipeLinks(text) ++
        pipeLinks.values
      ).toSet
  }

  /**
   * Returns all links given the surface form of one
   */
  def getAllFormsLinkSet(text: String): Iterable[String] = {
    val pipeLinks = getPipeLinksMap(text)
    (
      getNonPipeLinks(text) ++
        pipeLinks.values ++
        pipeLinks.keys
      ).toSet
  }

  /**
   * Checking if WikiPage IS an article
   */
  def isCoolPage(page: WikiPage) = !page.isRedirect && !page.isSpecialPage && !page.isDisambiguationPage && !page.isStub
}
