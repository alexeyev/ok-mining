package ru.stachek66.okminer

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.{VerbDetector, StopWordsFilter, Lexer, Tokenizer}
import ru.stachek66.okminer.wiki._
import ru.stachek66.okminer.wiki.translation.Tool
import ru.stachek66.okminer.wiki.vocabulary.Vocabulary
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.util.{Success, Failure, Try}

/**
 * @author alexeyev
 */
object TrendsTool {
  //extends App {

  //todo: try article as a request
  private val log = LoggerFactory.getLogger("test-tool")

  import ExecutionContext.Implicits.global

  log.info("Opening index.")
  articles.IndexProperties.index
  keyphrases.IndexProperties.index

  val dummy = "dummyword"
  val testText = "Приложение от компании FacialNetwork.com вызвало возмущение у представителей правозащитных организаций. Они логично предположили, что подобные программы могут распознавать лица прохожих без их разрешения, что станет настоящим кошмаром с точки конфиденциальности. Можно будет автоматически распознавать на улице людей с криминальной историей, бывших преступников, замужних/незамужних/разведенных женщин, людей нетрадиционной сексуальной ориентации и т.д. (фильтрация по профилям Facebook).\n\nРазработчики NameTag категорически отвергают опасения правозащитников. Наоборот, они считают, что распознавание лиц прохожих сделает нашу жизнь интереснее: «Я верю, что это сделает онлайновые свидания и офлайновые отношения гораздо безопаснее и позволит нам лучше понять окружающих людей, — говорит автор программы Кевин Алан Тасси (Kevin Alan Tussy). — Гораздо проще встретить интересных людей, просто оглядываясь вокруг, посмотреть профиль Facebook, изучить страницу LinkedIn и, может быть, профиль на сайте знакомств. Раньше нам приходилось знакомиться с людьми вслепую или не знакомиться вовсе. NameTag для Google Glass изменит это».\n\nТехнология явно имеет большие перспективы. И как бы не возмущались правозащитники, и даже если Google пока запрещает подобные приложения для Google Glass, но технический прогресс остановить невозможно. И если быстрое сканирование миллионов фотографий станет технически возможным, то можно не сомневаться, что подобные приложения станут популярными. Тем более, что программы распознавания лиц уже вовсю работают в казино и многих магазинах для автоматического распознавания известных мошенников и воришек на входе в заведение.\n\nВ данный момент вышла закрытая бета-версия NameTag для Google Glass. До конца I кв. 2014 года разработчики обещают выпустить версии для Android и iOS. "

  def extractTrends(text: String): Iterable[String] = {

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
          val kp = Future(KeyphrasenessCalculator.getKeyPhraseness(phrase))
          val res =
            Try {
              Await.result(kp, 1 seconds)
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

    val ranked = dResults.toSeq.sortBy(-_._2).take(5)

    val translation = ranked.map {
      case (terms, score) => (score, terms, Tool.translate(terms))
    }

    log.debug("Duples:\n" + translation.mkString("\n"))

    //todo: rewrite
    (translation.map {
      case (score, terms, Some((ruArticle, enArticle))) => Some(ruArticle)
      case _ => None
    }).toIterable.filter(_.isDefined).map(_.get)
  }
}