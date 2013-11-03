package ru.stachek66.okminer.language

import java.io.StringReader
import org.apache.lucene.analysis.{Token, TokenStream}
import org.apache.lucene.analysis.tokenattributes.{OffsetAttribute, TermAttributeImpl, TermAttribute}
import org.apache.lucene.morphology.russian.RussianAnalyzer

/**
 * @author alexeyev
 */
object RussianTokenizer {

  private lazy val russian = new RussianAnalyzer()

  /**
   * Lucene tokenizer applied to text.
   * @param string
   */
  def tokenize(string: String) {
    val stream = russian.tokenStream(null, new StringReader(string))

    while (stream)

    tok(stream)
  }

  def main(args: Array[String]) {
    //    val wordBaseForms = luceneMorph.getNormalForms("красивейший? величайший, однако, закатец")
    //    println(wordBaseForms)
    println(tokenize("привет, подушка, продувай? БОУБК, БОУЧНАЯ"))
  }

}
