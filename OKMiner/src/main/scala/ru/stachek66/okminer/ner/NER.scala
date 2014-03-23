package ru.stachek66.okminer.ner

/**
 * Abstract train representing the NER functionality.
 * @author alexeyev
 */
trait NER {
  def extractAllCompanies(sourceText: String): Set[String]
}
