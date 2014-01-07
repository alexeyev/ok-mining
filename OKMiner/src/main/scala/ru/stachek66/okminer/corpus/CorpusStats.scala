package ru.stachek66.okminer.corpus

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.Tokenizer
import ru.stachek66.okminer.utils.FileUtils

/**
 * @author alexeyev
 */
object CorpusStats {

  import Tokenizer._
  import ru.stachek66.okminer.language.russian.StopWordsFilter.{filter => stopFilter}

  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val bags: Map[String, Map[String, Int]] = {
    for {
      file <- tfIdfDirectory.listFiles().toIterable
      if file.isFile
    } yield {
      log.info("Reading " + file.getName)
      file.getName -> {
        {
          for {
            line <- FileUtils.asString(file).split("\n").toIterable
            if !line.isEmpty
            pair = line.split("\t")
            word = pair(0)
            freq = pair(1).toInt
          } yield word -> freq
        } toMap
      }
    }
  } toMap

  def docsInCorpus: Int = bags.keys.size

  def docsInCorpus(term: String): Int =
    bags.count {
      case (doc, bag) =>
        bag.keySet.contains(tokenize(term).mkString(""))
    }
}
