package ru.stachek66.okminer

import categories.TechCategories
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.{VerbDetector, StopWordsFilter, Lexer, Tokenizer}
import ru.stachek66.okminer.wiki._
import ru.stachek66.okminer.wiki.translation.Translator
import ru.stachek66.okminer.wiki.vocabulary.Vocabulary
import scala.concurrent.duration._
import scala.concurrent._
import scala.concurrent.Await
import scala.util.{Success, Failure, Try}

/**
 * Trends extraction.
 * @author alexeyev
 */
private[okminer] class TrendsTool(kpCalculator: KeyphrasenessCalculator = SmoothedKeyphrasenessCalculator,
                                  translator: Translator = new Translator()) {

  private val log = LoggerFactory.getLogger("trends-tool")

  log.info("Opening index.")
  articles.IndexProperties.index
  keyphrases.IndexProperties.index

  val dummy = "dummyword"

  /**
   * Provided with some text, returns most valuable wiki-trends.
   * @param text  source text
   * @return  resulting Wikipedia titles
   */
  def extractTrends(text: String): Iterable[(Double, String, String, String)] = {

    val splitted = Lexer.split(text)
    val filtered = splitted.map(t => if (StopWordsFilter.getList.contains(t)) dummy else t)

    val tokens =
      for {
        word <- filtered
        if !StopWordsFilter.getList.contains(word)
        if !VerbDetector.isVerb(word)
        tokenized <- Tokenizer.tokenize(word).headOption
        if !StopWordsFilter.getList.contains(tokenized)
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
          val kp = future[Double](kpCalculator.getKeyPhraseness(phrase))(ru.stachek66.okminer.Meta.singleContext)
          val res =
            Try {
              Await.result(kp, 10 seconds)
            } match {
              case Failure(e) => {
                log.debug("Takes too long: %s".format(phrase))
                None
              }
              case Success(kps) => {
                if (kps > 0) {
                  Some((phrase, kps))
                } else None
              }
            }
          res
        }
      }
    }.flatten.toSet

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

    for {
      (score, terms, optTranslation) <- translation
      (ru, en) <- optTranslation
      normalizedEnglish = categories.Utils.norm(en)
      if TechCategories.acceptableTopics.contains(normalizedEnglish)
    } yield (score, terms, ru, normalizedEnglish)
  }
}