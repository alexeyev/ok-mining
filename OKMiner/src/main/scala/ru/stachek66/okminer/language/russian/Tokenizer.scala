package ru.stachek66.okminer.language.russian

import java.io.StringReader
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import ru.stachek66.okminer.Meta

//import org.apache.lucene.analysis.tokenattributes.TermAttribute
//import org.apache.lucene_old.russian.RussianAnalyzer

import org.apache.lucene.analysis.ru.RussianAnalyzer
import scala.collection.mutable.ArrayBuffer

/**
 * Tokenization + stemming provided by Lucene RussianAnalyzer
 * @author alexeyev
 */
object Tokenizer {

  private lazy val russian = new RussianAnalyzer(Meta.luceneVersion)

  /**
   * Lucene tokenizer applied to text.
   */
  def tokenize(string: String): Iterable[String] = {
    val deёzated = string.toLowerCase.replace("ё", "е")
    val stream = russian.tokenStream("", new StringReader(deёzated))
    val result = ArrayBuffer[String]()
    stream.reset()
    while (stream.incrementToken()) {
      result +=
        stream.getAttribute(classOf[CharTermAttribute]).toString
    }
    stream.close()
    result
  }
}
