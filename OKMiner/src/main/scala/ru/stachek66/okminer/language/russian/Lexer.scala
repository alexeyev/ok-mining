package ru.stachek66.okminer.language.russian

import java.io.StringReader
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import ru.stachek66.okminer.Meta
import cc.mallet.extract.StringTokenization
import cc.mallet.util.CharSequenceLexer
import java.util.regex.Pattern
import cc.mallet.pipe.CharSequenceRemoveHTML

//import org.apache.lucene.analysis.tokenattributes.TermAttribute
//import org.apache.lucene.morphology.russian.RussianAnalyzer

import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.apache.lucene.util._
import scala.collection.mutable.ArrayBuffer

/**
 * @author alexeyev
 */
object Lexer extends App {

  //todo: version to properties
  private lazy val russian = new RussianAnalyzer(Meta.luceneVersion)

  /**
   * Lucene tokenizer applied to text.
   * @param string
   */
  def split(string: String): Iterable[String] = {
    val lexer =
      new CharSequenceLexer(
        string.asInstanceOf[CharSequence],
        Pattern.compile("[\\p{LD}]+([\\.-][\\p{LD}]+)*"))
    val buffer = ArrayBuffer[String]()
    while (lexer.hasNext) {
      buffer += lexer.next.asInstanceOf[String]
    }
    buffer
  }
}
