package ru.stachek66.okminer.language.russian

import org.apache.lucene_old.russian.RussianMorphology
import scala.collection.JavaConversions._

/**
 * АОТ-project-based lemmatization: Zaliznyak grammar rules.
 * https://code.google.com/p/russianmorphology/
 *
 * Won't lemmatize a word if it contains symbols from [A-z0-9\\.]
 *
 * @author alexeyev
 */
object Lemmatizer {

  private val latinRegex = ".*[A-Za-z0-9\\.;:].*".r.pattern

  def lemmatize(text: String): Iterable[String] = {
    val b = new RussianMorphology()
    if (!latinRegex.matcher(text).matches()) {
      b.getNormalForms(text)
    } else {
      List(text)
    }
  }
}
