package ru.stachek66.okminer.language.russian

import java.io.File
import scala.sys.process._
import scala.parallel._
import scala.collection.JavaConversions._

/**
 * Snowball stemmer wrapper
 * @author alexeyev
 */
object Stemmer {

  require(new File("tools").exists())

  def stem(word: String): String =
    ("echo %s".format(word) #| "./tools/stemwords -l russian").!!

  def stem(words: Iterable[String]): Iterable[String] = {
    println("stemming " + words.size + " " + words)
    words.map(stem(_))
  }
}
