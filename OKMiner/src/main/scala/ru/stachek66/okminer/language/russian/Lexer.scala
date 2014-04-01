package ru.stachek66.okminer.language.russian

import java.io.StringReader
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import ru.stachek66.okminer.Meta
import cc.mallet.extract.StringTokenization
import cc.mallet.util.CharSequenceLexer
import java.util.regex.Pattern
import cc.mallet.pipe.CharSequenceRemoveHTML

//import org.apache.lucene.analysis.tokenattributes.TermAttribute
//import org.apache.lucene_old.russian.RussianAnalyzer

import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.apache.lucene.util._
import scala.collection.mutable.ArrayBuffer

/**
 * Plain regex-based lexer.
 * @author alexeyev
 */
object Lexer {

  /**
   * Lucene tokenizer applied to text.
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
