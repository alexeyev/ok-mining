package ru.stachek66.okminer.corpus

import ru.stachek66.okminer.utils.FileUtils
import ru.stachek66.okminer.language.russian.{Tokenizer, StopWordsFilter, Stemmer}
import java.io.File

/**
 * @author alexeyev
 */
object CorpusStats {

  import Stemmer._
  import StopWordsFilter.{filter => stopFilter}
  import Tokenizer._

  lazy val bags: Map[String, Set[String]] = {
    directory.
      listFiles().
      filter(_.isFile).
      map {
      file: File =>
        file.getName ->
          stem(
            stopFilter(
              tokenize(
                FileUtils.asStringWithoutNewLines(file)))).toSet

    }.toMap
  }

  def docsInCorpus: Int = bags.keys.size

  def docsInCorpus(term: String): Int =
    bags.count {
      case (doc, bag) =>
        bag.contains(
          stem(
            tokenize(term).mkString("")))
    }
}
