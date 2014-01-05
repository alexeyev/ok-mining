package ru.stachek66.okminer.ner

/**
 * @author alexeyev
 */
object HeuristicsHelper {

  def isDigits(s: String) = s.forall(Character.isDigit(_))

}
