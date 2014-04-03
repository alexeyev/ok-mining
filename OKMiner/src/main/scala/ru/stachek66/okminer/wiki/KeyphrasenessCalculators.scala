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
@deprecated // since April, filtering by category eliminates problem of ranking introduction necessity
object SmoothedKeyphrasenessCalculator extends KeyphrasenessCalculator {

  private val phraseSearcher = new articles.PhraseSearcher(articles.IndexProperties.index)
  private val keywordsSearcher = new keyphrases.Searcher(keyphrases.IndexProperties.index)

  /**
   * Relative TF with simple smoothing.
   */
  override def getKeyPhraseness(phrase: String): Double =
    keywordsSearcher.getHitsCount(phrase) / (phraseSearcher.getHitsCount(phrase) + 1.0)
}

/**
 * Keyohraseness as tf with smoothing (+ 1 at denominator).
 */
object DummyKeyPhrasenessCalculator extends KeyphrasenessCalculator {

  /**
   * Returns 1.
   */
  override def getKeyPhraseness(phrase: String): Double = 1.0
}