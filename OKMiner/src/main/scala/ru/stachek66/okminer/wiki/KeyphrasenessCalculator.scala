package ru.stachek66.okminer.wiki

import org.slf4j.LoggerFactory

/**
 * @author alexeyev
 */
object KeyphrasenessCalculator {

  def getKeyPhraseness(phrase: String): Double = {
    val keys = keyphrases.Searcher.tryPhrase(phrase)
    val all = articles.Searcher.tryPhrase(phrase)
    keys.size / (all.size + 0.01)
  }

  def main(args:Array[String]) {
    keyphrases.Searcher.tryPhrase("нурофен")
  }
}
