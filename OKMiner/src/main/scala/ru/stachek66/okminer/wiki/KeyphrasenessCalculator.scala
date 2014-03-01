package ru.stachek66.okminer.wiki


/**
 * @author alexeyev
 */
object KeyphrasenessCalculator {

  private lazy val phraseSearcher = new articles.PhraseSearcher()

  def getKeyPhraseness(phrase: String): Double =
    (keyphrases.Searcher.getHitsCount(phrase) + 1) / (phraseSearcher.getHitsCount(phrase) + 10)

  def main(args: Array[String]) {
    println(getKeyPhraseness("космическая пыль"))
  }
}
