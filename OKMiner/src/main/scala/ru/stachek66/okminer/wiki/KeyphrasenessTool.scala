package ru.stachek66.okminer.wiki

/**
 * @author alexeyev
 */
object KeyphrasenessTool extends App {

  val phrase = "математическая лингвистика"
  println(keyphrases.Searcher.getHitsCount(phrase))
  println(articles.Searcher.getHitsCount(phrase))

  println()

  val phrase0 = "математическая ярость топора"
  println(keyphrases.Searcher.getHitsCount(phrase0))
  println(articles.Searcher.getHitsCount(phrase0))
}
