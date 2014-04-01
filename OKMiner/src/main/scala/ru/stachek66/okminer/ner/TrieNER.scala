package ru.stachek66.okminer.ner

import ru.stachek66.okminer.utils.{CounterLogger, Trie}
import ru.stachek66.okminer.language.russian.Lexer
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._

/**
 *
 * @author alexeyev
 */
class TrieNER extends NER {

  import TrieNER._

  def extractAllCompanies(sourceText: String): Set[String] = {
    val normalizedText = normalize(
      HeuristicsHelper.replaceCommas(
        HeuristicsHelper.replaceUrls(sourceText)))

    normalizedText.tails.flatMap(tail => {
      val matcch = trie.foundPrefixes(tail)
      for (top <- matcch)
      yield tail.substring(0, top).trim
    }).toSet
  }

}

object TrieNER {

  private val clog = new CounterLogger(LoggerFactory.getLogger("trie-ner"), 50000, "%s companies put")

  private def normalize(sourceText: String) = " " + Lexer.split(sourceText).mkString(" ") + " " // space as a special delimiter

  private val trie = {
    val ttrie = new Trie()
    accessSources(line => clog.execute {
      ttrie.add(normalize(line))
      //      ttrie.add(normalize(line.toLowerCase))
    })
    ttrie
  }

}
