package ru.stachek66.okminer.language.russian

import org.apache.lucene_old.russian.RussianMorphology
import scala.collection.JavaConversions._

/**
 * АОТ-project-based lemmatization: Zaliznyak grammar rules.
 * https://code.google.com/p/russianmorphology/
 *
 * Won't lemmatize a word if it contains symbols from [A-z0-9\\.]
 * Is VERY slow, which is why it shouldn't be used for our purposes.
 *
 * @author alexeyev
 */
@deprecated // since April 1 for being too slow
object Lemmatizer {

  /**
   * Regex for checking if a word can be lemmatized by AOT lemmatizer.
   */
  private val latinRegex = ".*[^А-Яа-я-].*".r.pattern

  /**
   * @param sourceText text to be lemmatized
   * @return normal forms of tokens
   */
  def lemmatize(sourceText: String): Iterable[String] = {
    val nText = sourceText.toLowerCase.replaceAll("ё", "е")
    for {
      token <- Lexer.split(nText)
    } yield {
      if (latinRegex.matcher(token).matches) token
      else lemmatizeToken(token)
    }
  }

  /**
   * @param text Russian lexem
   * @return normal form
   */
  private def lemmatizeToken(text: String): String = {
    val b = new RussianMorphology()
    if (!latinRegex.matcher(text).matches()) {
      b.getNormalForms(text).head
    } else {
      text
    }
  }
}
