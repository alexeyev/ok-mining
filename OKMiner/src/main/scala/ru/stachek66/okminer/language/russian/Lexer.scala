package ru.stachek66.okminer.language.russian

import cc.mallet.util.CharSequenceLexer
import java.util.regex.Pattern

//import org.apache.lucene.analysis.tokenattributes.TermAttribute
//import org.apache.lucene_old.russian.RussianAnalyzer

import scala.collection.mutable.ArrayBuffer

/**
 * Plain regex-based lexer.
 * @author alexeyev
 */
object Lexer {

  /**
   * Mallet's lexer applied to text.
   * @param string the text in concern
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
