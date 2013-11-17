package ru.stachek66.okminer.keywords

/**
 * @author alexeyev
 */
abstract class TfIdf {
  def eval(term: String, termCount: Int, maxTermCount: Int): Double
}
