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
  log.info("Keyphraseness calculator = " + kpCalculator.getClass)

  //making sure these guys are ready
  articles.IndexProperties.index
  keyphrases.IndexProperties.index
  TechCategories.acceptableTopics

  /**
   * the non-existing word that won't match anything
   */
  private val dummy = "dummyword"

  /**
   * Provided with some text, returns most valuable wiki-trends.
   * @param text  source text
   * @return  resulting Wikipedia titles
   */
  def extractTrends(text: String): Iterable[(Double, String, String, String)] = {

    val splitted = Lexer.split(text)

    //    println(splitted)

    val filtered = splitted.map(t => if (StopWordsFilter.stopList.contains(t)) dummy else t)

    //    println(filtered)

    val tokens =
      for {
        word <- filtered
        tokenized <- Tokenizer.tokenize(word).headOption
      } yield {
        //        print("[" + tokenized + "] ")
        if (Vocabulary.normalizedWords.contains(tokenized)) word
        else dummy
      }
    //    println()
    //    println(tokens)

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

    log.trace("Duples:\n" + translation.mkString("\n"))

    log.debug("Translation done.")

    val result = (
      for {
        (score, terms, optTranslation) <- translation
        (ru, en) <- optTranslation
        normalizedEnglish = categories.Utils.norm(en)
        if TechCategories.acceptableTopics.contains(normalizedEnglish)
      } yield {
        var topics: Iterable[String] = TechCategories.acceptableTopics(normalizedEnglish)
        val buffer = collection.mutable.ArrayBuffer[String]()
        var maxUpper = 1
        while (topics.nonEmpty && maxUpper >= 0) {
          maxUpper -= 1
          buffer ++= topics
          topics = topics.flatMap {
            topic =>
              TechCategories.acceptableTopics(topic)
          }
        }
        buffer.map {
          case entopic =>
            (score, terms, ru, entopic)
        }
      }
      ).flatten

    log.debug("After filtering:\n" + result.mkString("\n"))

    if (JsonConfigReader.config.useKeyPhrasenessThreshold) result.take(5)
    else result
  }
}