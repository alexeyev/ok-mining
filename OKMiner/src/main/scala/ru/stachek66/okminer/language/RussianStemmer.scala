package ru.stachek66.okminer.language

import java.io.File
import scala.sys.process._

/**
 * Snowball stemmer wrapper
 * @author alexeyev
 */
object RussianStemmer {

  require(new File("tools").exists())

  def stem(word: String): String = {
    "echo '%s'" format word #|
      "./tools/stemwords -l russian" linesIterator
  }.next()

  def stem(words: Iterable[String]): Iterable[String] =
    words.map(
      word => stem(word)
    )
}
