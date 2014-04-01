package ru.stachek66.okminer.ner.tree

import ru.stachek66.okminer.utils.CounterLogger
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.{Lexer, Lemmatizer}
import com.googlecode.concurrenttrees.radixinverted.ConcurrentInvertedRadixTree
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory
import ru.stachek66.okminer.ner.{HeuristicsHelper, NER}
import scala.collection.JavaConversions._

/**
 * Using radix tree as a fast way to find all required substrings
 * (that is, NE-s) in the text.
 * @author alexeyev
 */
class InvertedRadixTreeNER extends NER {

  import InvertedRadixTreeNER._

  def extractAllCompanies(sourceText: String): Set[String] = {
    clog.getLogger.info("Normalizing text...")
    val normalizedText = normalizeText(
      HeuristicsHelper.replaceCommas(
        HeuristicsHelper.replaceUrls(sourceText)))
    clog.getLogger.info("Extracting companies from text...")
    tree.getKeysContainedIn(normalizedText).map(_.toString.trim).toSet
  }
}

object InvertedRadixTreeNER {

  import ru.stachek66.okminer.ner._

  private val clog = new CounterLogger(LoggerFactory.getLogger("inverted-radix-tree-ner"), 50000, "%s companies put")

  private def normalizeText(sourceText: String) =
    " " +
      Lemmatizer.lemmatize(sourceText).mkString(" ") +
      " " // space as a special delimiter

  private def normalizeCompany(sourceText: String) =
    " " +
      Lexer.split(sourceText.toLowerCase).mkString(" ") +
      " " // space as a special delimiter

  private val tree = {
    val tree = new ConcurrentInvertedRadixTree[Boolean](new DefaultCharArrayNodeFactory())

    accessSources(
      line =>
        clog.execute {
          tree.put(normalizeCompany(line), true)
        }
    )
    tree
  }
}
