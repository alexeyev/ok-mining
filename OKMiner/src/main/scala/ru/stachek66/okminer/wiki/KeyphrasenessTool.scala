package ru.stachek66.okminer.wiki

/**
 * @author alexeyev
 */
object KeyphrasenessTool extends App{

  def getKeyPhraseness(phrase: String): Double = {
    val keys = keyphrases.Searcher.tryPhrase(phrase).size
    val all = articles.Searcher.tryPhrase(phrase).size
    keys / (all + 0.01)
  }
//
  val phrase = "эта"
  println(keyphrases.Searcher.tryPhrase(phrase).size)
  println(articles.Searcher.tryPhrase(phrase).size)
//  //  println(keyphrases.Searcher.tryPhrase(phrase).mkString("\n"))
//  //  println(articles.Searcher.getHitsCount(phrase))
//
//  println()
//  //
//  //  val phrase0 = "математическая ярость топора"
//  //  println(keyphrases.Searcher.getHitsCount(phrase0))
//  //  println(articles.Searcher.getHitsCount(phrase0))
}
