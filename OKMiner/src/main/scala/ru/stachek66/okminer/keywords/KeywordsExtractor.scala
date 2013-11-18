package ru.stachek66.okminer.keywords

import java.io.File
import org.apache.commons.collections.bag.HashBag
import ru.stachek66.okminer.language.russian.StopWordsFilter
import ru.stachek66.okminer.language.russian.Tokenizer._
import ru.stachek66.okminer.utils.FileUtils
import scala.collection.JavaConversions._


/**
 * @author alexeyev
 */
class KeywordsExtractor {

  def getRanked(text: String, formula: TfIdf): Map[String, Double] = {
    val wellPreparedTokens =
      StopWordsFilter.filter(
        tokenize(text))

    //counting frequencies
    val bag = new HashBag()
    for (t <- wellPreparedTokens) {
      bag.add(t)
    }
    val freqsMap = bag.uniqueSet().map(
      str => str.asInstanceOf[String] -> bag.getCount(str)).toMap[String, Int]
    val maxFreq = freqsMap.values.max
    freqsMap.map {
      case (word, freq) =>
        word -> formula.eval(word, freq, maxFreq)
    }
  }
}

object Test {
  @deprecated
  def main(args: Array[String]) {
    //    println(CorpusStats.docsInCorpus)
    //println(CorpusStats.bags)

    //    println(CorpusStats.docsInCorpus("скачаю"))

    println(new KeywordsExtractor().getRanked(
      FileUtils.asStringWithoutNewLines(new File("corpus/9.txt")),
      new BasicTfIdf
    ))
  }
}
