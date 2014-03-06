package ru.stachek66.okminer.ner

/**
 * @author alexeyev
 */
object HeuristicsHelper {

  def isDigits(s: String) = s.forall(Character.isDigit(_))

  private val urlPattern = "(https?://)?[a-zа-я0-9-_]+(\\.[a-zа-я-_0-9]+)+(/[A-zА-я0-9-_#\\?]+)+/?".r

  def replaceUrls(s: String) = {
    urlPattern.replaceAllIn(s, " ")
  }

}
