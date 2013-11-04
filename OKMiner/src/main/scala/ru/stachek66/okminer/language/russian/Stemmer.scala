package ru.stachek66.okminer.language.russian

import java.io.File
import scala.sys.process._

/**
 * Snowball stemmer wrapper
 * @author alexeyev
 */
object Stemmer {

  require(new File("tools").exists())

  def stem(word: String): String =
    ("echo %s".format(word) #| "./tools/stemwords -l russian").!!

  def stem(words: Iterable[String]): Iterable[String] = words.map(stem(_))
}
