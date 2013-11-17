package ru.stachek66.okminer.language.russian

import java.io.File
import scala.sys.process._
import scala.concurrent

/**
 * Snowball stemmer wrapper
 * Upd: an awful one, disabled.
 * @author alexeyev
 */
object Stemmer {

  require(new File("tools").exists())

  def stem(word: String): String =
    ("echo '%s'".format(word) #| "./tools/stemwords -l russian").!!

  def stem(words: Iterable[String]): Iterable[String] = {
    println("stemming this: " + words.size + " " + words)
    val stemmed = words.map(word => {
      stem(word).trim()
    })

    println("stemming done.")
    stemmed
  }
}
