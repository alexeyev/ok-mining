package ru.stachek66.okminer.wiki.translation

import ru.stachek66.okminer.wiki._

/**
 * Translation tool; given an article name in Russian.
 * looks it up in the translations index.
 * @author alexeyev
 */
class Translator(index: Index = new Index(parsed("ru_en"), indices("ru_en"))) {

  def translate(topic: String): Option[IndexProperties.RuEn] = {
    val s = new Searcher(index)
    s.search(topic).headOption.map {
      case (score, (ru, en)) => (ru, en)
    }
  }
}
