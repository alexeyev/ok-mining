package ru.stachek66.okminer.language.russian

import java.io.StringReader
import org.apache.lucene.analysis.tokenattributes.TermAttribute
import org.apache.lucene.morphology.russian.RussianAnalyzer
import scala.collection.mutable.ArrayBuffer

/**
 * @author alexeyev
 */
object Tokenizer {

  private lazy val russian = new RussianAnalyzer()

  /**
   * Lucene tokenizer applied to text.
   * @param string
   */
  def tokenize(string: String): Iterable[String] = {
    println("tokenizing this: " + string)
    val stream = russian.tokenStream(null, new StringReader(string))
    val result = ArrayBuffer[String]()
    stream.reset()
    while (stream.incrementToken()) {
      result += stream.getAttribute(classOf[TermAttribute]).term()
    }
    result
  }
}
