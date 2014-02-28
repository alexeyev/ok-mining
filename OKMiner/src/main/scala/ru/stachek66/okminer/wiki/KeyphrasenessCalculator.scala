package ru.stachek66.okminer.wiki


/**
 * @author alexeyev
 */
object KeyphrasenessCalculator {

  def getKeyPhraseness(phrase: String): Double =
    keyphrases.Searcher.getHitsCount(phrase) / (new articles.PhraseSearcher().getHitsCount(phrase) + 0.01)

  def main(args: Array[String]) {
    println(getKeyPhraseness("космическая пыль"))
  }
}
