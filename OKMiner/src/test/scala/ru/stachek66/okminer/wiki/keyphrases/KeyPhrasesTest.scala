package ru.stachek66.okminer.wiki.keyphrases

/**
 * Testing whether keyphrases search is reasonable
 * @author alexeyev
 */
object KeyPhrasesTest extends App {

  val s = new Searcher(new Index())
  println(s.tryPhrase("облачное хранилище"))
  println(s.tryPhrase("облачное вычисления"))
  println(s.tryPhrase("интернет вещица"))
  println(s.tryPhrase("интернет вещей"))
  println(s.tryPhrase("смартфон "))

}
