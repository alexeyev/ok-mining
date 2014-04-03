package ru.stachek66.okminer.ner

/**
 * Abstract trait representing the NER functionality
 * @author alexeyev
 */
trait NER {
  /**
   * Provided with a text in Russian language, returns a list of recognized organizations
   * @param sourceText Russian text
   * @return organizations
   */
  def extractAllCompanies(sourceText: String): Set[String]
}
