package ru.stachek66.okminer.keywords

import org.apache.commons.collections.bag.HashBag
import ru.stachek66.okminer.corpus.CorpusStats
import ru.stachek66.okminer.language.russian.StopWordsFilter
import ru.stachek66.okminer.language.russian.Tokenizer._
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
      "«Зенит» U-17 обыграл «Крылья Советов»\nКоманда Академии 1996 года рождения с победы начала выступление на первенстве России среди юношеских команд клубов Премьер-лиги и ФНЛ.\n\nВ стартовом матче группового этапа первенства подопечные Александра Селенкова и Евгения Тарасова обыграли самарские «Крылья Советов» со счетом 2:1. Голами в составе петербуржцев отметились Павел Долгов и Павел Назимов.\n\nСледующий матч на турнире зенитовцы проведут 16 октября против пермского «Амкара». ",
      new BasicTfIdf
    ))

  }
}
