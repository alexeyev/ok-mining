package ru.stachek66.okminer.ner.tree

import ru.stachek66.okminer.utils.{CounterLogger, Trie}
import ru.stachek66.okminer.language.russian.Lexer
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._
import ru.stachek66.okminer.ner.{HeuristicsHelper, NER}

/**
 * Using Trie for finding all required substrings (that is, organizations) in the text.
 * @author alexeyev
 */
@deprecated // since April, using InvertedRadixTreeNER helps to extract companies more efficiently
class TrieNER extends NER {

  import TrieNER._

  /**
   * @param sourceText text in Russian
   * @return recognized companies, if any
   */
  def extractAllCompanies(sourceText: String): Set[String] = {
    val normalizedText = normalize(
      HeuristicsHelper.replaceCommas(
        HeuristicsHelper.replaceUrls(sourceText)))

    normalizedText.tails.flatMap(
      tail => {
        val matcch = trie.foundPrefixes(tail)
        for (top <- matcch)
        yield tail.substring(0, top).trim
      }).toSet
  }

}

@deprecated // since April, use InvertedRadixTreeNER instead
object TrieNER {

  import ru.stachek66.okminer.ner._

  private val clog = new CounterLogger(LoggerFactory.getLogger("trie-ner"), 50000, "%s companies put")

  private def normalize(sourceText: String) = " " + Lexer.split(sourceText.replaceAll("ё", "е")).mkString(" ") + " " // space as a special delimiter

  /**
   * All companies represented as a trie
   */
  private val trie = {
    val ttrie = new Trie()
    accessSources {
      case line =>
        clog.execute {
          ttrie.add(normalize(line))
        }
    }
    ttrie
  }
}