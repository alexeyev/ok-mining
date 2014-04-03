package ru.stachek66.okminer.ner

/**
 * Methods for helping to do named-entity recognition.
 * All necessary heuristics are placed here.
 * @author alexeyev
 */
object HeuristicsHelper {

  private val httpUrlPattern = "(https?://)?[a-zа-я0-9-_]+(\\.[a-zа-я-_0-9]+)+(/[A-zА-я0-9-_#\\?]+)+/?".r

  def isDigits(s: String) = s.forall(Character.isDigit(_))

  def replaceUrls(s: String) = httpUrlPattern.replaceAllIn(s, " ")

  def replaceCommas(s: String) = s.replaceAll(", ", " symbolnottobesearchd0 ")

}
