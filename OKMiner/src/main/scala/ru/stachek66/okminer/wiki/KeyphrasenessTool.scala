package ru.stachek66.okminer.wiki

import org.slf4j.LoggerFactory

/**
 * @author alexeyev
 */
object KeyphrasenessTool extends App{

  private val log = LoggerFactory.getLogger("key-tool")

  def getKeyPhraseness(phrase: String): Double = {
    val keys = keyphrases.Searcher.tryPhrase(phrase)
//    log.info(keys.toString())
    val all = articles.Searcher.tryPhrase(phrase)
//    log.info(all.toString())
    keys.size / (all.size + 0.01)
  }
}
