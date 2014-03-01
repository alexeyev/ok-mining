package ru.stachek66.okminer.language.russian

import java.io.File


/**
 * Snowball stemmer wrapper
 * Upd: an awful one, disabled.
 *
 * @author alexeyev
 */
@deprecated
object Stemmer {
  //todo: use mystem
  //  require(new File("tools").exists())
  //  def stem(word: String): String =
  //    ("echo '%s'".format(word) #| "./tools/stemwords -l russian").!!
  //

  @deprecated
  def stem(words: Iterable[String]): Iterable[String] = words
}
