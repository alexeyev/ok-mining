package ru.stachek66.okminer.wiki

/**
 * @author alexeyev
 */
object KeyphrasenessTool extends App {

  val phrase = "интеллектуальный анализ данных"
  println(keyphrases.Searcher.tryPhrase(phrase).size)
  println(articles.Searcher.tryPhrase(phrase).size)
//  println(keyphrases.Searcher.tryPhrase(phrase).mkString("\n"))
  //  println(articles.Searcher.getHitsCount(phrase))

  println()
  //
  //  val phrase0 = "математическая ярость топора"
  //  println(keyphrases.Searcher.getHitsCount(phrase0))
  //  println(articles.Searcher.getHitsCount(phrase0))
}
