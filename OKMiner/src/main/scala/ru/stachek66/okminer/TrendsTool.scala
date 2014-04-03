package ru.stachek66.okminer

import categories.TechCategories
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.{StopWordsFilter, Lexer, Tokenizer}
import ru.stachek66.okminer.wiki._
import ru.stachek66.okminer.wiki.translation.Translator
import ru.stachek66.okminer.wiki.vocabulary.Vocabulary

/**
 * Trends extraction.
 * @author alexeyev
 */
private[okminer] class TrendsTool(kpCalculator: KeyphrasenessCalculator =
                                  if (JsonConfigReader.config.useKeyPhrasenessThreshold) SmoothedKeyphrasenessCalculator
                                  else DummyKeyPhrasenessCalculator,
                                  translator: Translator = new Translator()) {

  private val log = LoggerFactory.getLogger("trends-tool")

  log.info("Getting ready...")

  //making sure these guys are ready
  articles.IndexProperties.index
  keyphrases.IndexProperties.index
  TechCategories.acceptableTopics

  /**
   * the non-existing word that won't match anything
   */
  private val dummy = "dummy42word"

  /**
   * Provided with some text, returns most valuable wiki-trends.
   * @param text  source text
   * @return  resulting Wikipedia titles
   */
  def extractTrends(text: String): Iterable[(Double, String, String, String)] = {

    val splitted = Lexer.split(text)
    val filtered = splitted.map(t => if (StopWordsFilter.stopList.contains(t)) dummy else t)

    val tokens =
      for {
        word <- filtered
        tokenized <- Tokenizer.tokenize(word).headOption
      } yield {
        if (Vocabulary.normalizedWords.contains(tokenized)) word
        else dummy
      }

    val allDuples = tokens.zip(if (tokens.isEmpty) Seq() else tokens.tail)

    val duples = allDuples.filter {
      case (x, y) => !x.equals(dummy) && !y.equals(dummy)
    }

    def buildResults(phrases: Iterable[String]): Iterable[(String, Double)] = {
      phrases.map {
        phrase => {
          val kp = kpCalculator.getKeyPhraseness(phrase)
          if (kp > 0) {
            Some((phrase, kp))
          } else None
        }
      }.flatten.toSet
    }

    val dResults: Iterable[(String, Double)] = {
      buildResults(
        duples.map {
          case (first, second) => "%s %s".format(first, second)
        })
    }

    val oResults = buildResults(tokens)

    val ranked = (oResults ++ dResults).toSeq.sortBy(-_._2)

    val translation = ranked.map {
      case (terms, score) => (score, terms, translator.translate(terms))
    }

    log.debug("Duples:\n" + translation.mkString("\n"))

    (
      for {
        (score, terms, optTranslation) <- translation
        (ru, en) <- optTranslation
        normalizedEnglish = categories.Utils.norm(en)
        if TechCategories.acceptableTopics.contains(normalizedEnglish)
      } yield {
        Iterable((score, terms, ru, normalizedEnglish),
          (score, terms, ru, TechCategories.acceptableTopics(normalizedEnglish)))
      }
      ).flatten
  }
}