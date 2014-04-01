package ru.stachek66.okminer.wiki


/**
 * Computing keyphraseness measure used for ranking trends.
 * @author alexeyev
 */
trait KeyphrasenessCalculator {

  def getKeyPhraseness(phrase: String): Double

}

/**
 * Keyohraseness as tf with smoothing (+ 1 at denominator).
 */
object SmoothedKeyphrasenessCalculator extends KeyphrasenessCalculator {

  private val phraseSearcher = new articles.PhraseSearcher(articles.IndexProperties.index)
  private val keywordsSearcher = new keyphrases.Searcher(keyphrases.IndexProperties.index)

  /**
   * Relative TF with simple smoothing.
   */
  override def getKeyPhraseness(phrase: String): Double = 1

  //    keywordsSearcher.getHitsCount(phrase) / (phraseSearcher.getHitsCount(phrase) + 1.0)
}