package ru.stachek66.okminer.keywords

import java.io.File
import org.apache.commons.collections.bag.HashBag
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.NormalizationPipeConfig
import ru.stachek66.okminer.utils.FileUtils
import scala.collection.JavaConversions._


/**
 * @author alexeyev
 */
class KeywordsExtractor {

  private val log = LoggerFactory.getLogger(getClass)

  def getRanked(text: String, formula: TfIdf): Seq[(String, Double)] = {
    val wellPreparedTokens = NormalizationPipeConfig.pipe(text)
    log.info("Tokens prepared.")

    //counting frequencies
    val bag = new HashBag()
    for (t <- wellPreparedTokens) {
      bag.add(t)
    }
    val freqsMap = bag.uniqueSet().map(
      str =>
        str.asInstanceOf[String] -> bag.getCount(str)).toMap[String, Int]

    val maxFreq =
      if (freqsMap.values.isEmpty)
        0
      else
        freqsMap.values.max

    log.info("Frequencies counted. Computing tf-idf...")

    freqsMap.map {
      case (word, freq) =>
        (word, formula.eval(word, freq, maxFreq))
    }.toSeq.sortBy(-_._2)
  }
}

object TfIdfRunner {
//  @deprecated
//  def main(args: Array[String]) {
//    val rankedList = new KeywordsExtractor().getRanked(
//      FileUtils.asStringWithoutNewLines(new File("corpus/130.txt")),
//      new BasicTfIdf
//    )
//    val vector = rankedList.toList
//    for {v <- vector} println(v)
//  }
}
