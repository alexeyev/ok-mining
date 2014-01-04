package ru.stachek66.okminer.corpus

import java.io.File
import ru.stachek66.okminer.language.russian.{Tokenizer, Stemmer}
import ru.stachek66.okminer.utils.FileUtils

/**
 * @author alexeyev
 */
object CorpusStats {

  import Stemmer._
  import Tokenizer._
  import ru.stachek66.okminer.language.russian.StopWordsFilter.{filter => stopFilter}

  lazy val bags: Map[String, Map[String, Int]] = {
    tfIdfDirectory.
      listFiles().
      filter(_.isFile).
      map {
      file: File => {
        println("Reading " + file.getName)
        file.getName ->
          FileUtils.asString(file).split("\n").
            filterNot(_.equals("")).
            map {
            pair => {
              val splitted = pair.split("\t")
              splitted(0) -> splitted(1).toInt
            }
          }.toMap
      }
    }.toMap
  }

  def docsInCorpus: Int = bags.keys.size

  def docsInCorpus(term: String): Int =
    bags.count {
      case (doc, bag) =>
        bag.keySet.contains(tokenize(term).mkString(""))
    }
}
