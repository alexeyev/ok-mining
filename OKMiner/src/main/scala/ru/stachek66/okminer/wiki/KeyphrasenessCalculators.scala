package ru.stachek66.okminer.wiki


/**
 * Computing keyphraseness measure used for ranking trends.
 * @author alexeyev
 */
trait KeyphrasenessCalculator {

  def getKeyPhraseness(phrase: String): Double

}

/**
 * Keyohraseness as tf with Laplacian smoothing.
 */
object LaplacianKeyphrasenessCalculator extends KeyphrasenessCalculator {

  private val phraseSearcher = new articles.PhraseSearcher(articles.IndexProperties.index)
  private val keywordsSearcher = new keyphrases.Searcher(keyphrases.IndexProperties.index)

  /**
   * Relative TF with Laplacian smoothing.
   */
  override def getKeyPhraseness(phrase: String): Double =
    (keywordsSearcher.getHitsCount(phrase) /*/ (keyphrases.Searcher.totalDocs.toDouble)*/ + 1) /
      (phraseSearcher.getHitsCount(phrase).toDouble /*/ phraseSearcher.totalDocs.toDouble */ + phraseSearcher.totalDocs)
}