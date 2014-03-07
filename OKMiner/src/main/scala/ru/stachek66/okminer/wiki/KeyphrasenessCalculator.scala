package ru.stachek66.okminer.wiki

import ru.stachek66.okminer.wiki.articles.IndexProperties


/**
 * @author alexeyev
 */
object KeyphrasenessCalculator {

  private val phraseSearcher = new articles.PhraseSearcher(IndexProperties.index)

  /**
   * Relative TF with Laplacian smoothing.
   */
  def getKeyPhraseness(phrase: String): Double =
    (keyphrases.Searcher.getHitsCount(phrase) /*/ (keyphrases.Searcher.totalDocs.toDouble)*/ + 1) /
      (phraseSearcher.getHitsCount(phrase).toDouble /*/ phraseSearcher.totalDocs.toDouble */+ phraseSearcher.totalDocs)

  //
//    def main(args: Array[String]) {
//      println(getKeyPhraseness("космическая пыль"))
//    }
}