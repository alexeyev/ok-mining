package ru.stachek66.okminer.wiki.translation


/**
 * Translation tool; given an article name in Russian.
 * looks it up in the translations index.
 * @author alexeyev
 */
class Translator(index: Index = new Index()) {

  def translate(topic: String): Option[IndexProperties.RuEn] = {
    Searcher.search(topic).headOption.map {
      case (score, (ru, en)) => (ru, en)
    }
  }
}
