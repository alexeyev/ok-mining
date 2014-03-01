package ru.stachek66.okminer.language.russian

import java.io.StringReader
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import ru.stachek66.okminer.Meta

//import org.apache.lucene.analysis.tokenattributes.TermAttribute
//import org.apache.lucene.morphology.russian.RussianAnalyzer

import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.apache.lucene.util._
import scala.collection.mutable.ArrayBuffer

/**
 * @author alexeyev
 */
object Tokenizer extends App {

  //todo: version to properties
  private lazy val russian = new RussianAnalyzer(Meta.luceneVersion)

  /**
   * Lucene tokenizer applied to text.
   * @param string
   */
  def tokenize(string: String): Iterable[String] = {
    val stream = russian.tokenStream("", new StringReader(string))
    val result = ArrayBuffer[String]()
    stream.reset()
    while (stream.incrementToken()) {
      result +=
        stream.getAttribute(classOf[CharTermAttribute]).toString
    }
    stream.close()
    result
  }


  println(tokenize("Я сразу смазал карту будня, плеснувши краски из стакана"))
}
