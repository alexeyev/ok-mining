package ru.stachek66.okminer

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.{StopWordsFilter, Lexer, Tokenizer}
import ru.stachek66.okminer.wiki.KeyphrasenessCalculator
import ru.stachek66.okminer.wiki.vocabulary.Vocabulary
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.util.{Success, Failure, Try}
import ru.stachek66.okminer.wiki.keyphrases.Searcher

/**
 * @author alexeyev
 */
object FullPipeLineTool extends App {

  private val log = LoggerFactory.getLogger("test-tool")

  import ExecutionContext.Implicits.global

  val dummy = "dummyword"

  val testText = "Приложение от компании FacialNetwork.com вызвало возмущение у представителей правозащитных организаций. Они логично предположили, что подобные программы могут распознавать лица прохожих без их разрешения, что станет настоящим кошмаром с точки конфиденциальности. Можно будет автоматически распознавать на улице людей с криминальной историей, бывших преступников, замужних/незамужних/разведенных женщин, людей нетрадиционной сексуальной ориентации и т.д. (фильтрация по профилям Facebook).\n\nРазработчики NameTag категорически отвергают опасения правозащитников. Наоборот, они считают, что распознавание лиц прохожих сделает нашу жизнь интереснее: «Я верю, что это сделает онлайновые свидания и офлайновые отношения гораздо безопаснее и позволит нам лучше понять окружающих людей, — говорит автор программы Кевин Алан Тасси (Kevin Alan Tussy). — Гораздо проще встретить интересных людей, просто оглядываясь вокруг, посмотреть профиль Facebook, изучить страницу LinkedIn и, может быть, профиль на сайте знакомств. Раньше нам приходилось знакомиться с людьми вслепую или не знакомиться вовсе. NameTag для Google Glass изменит это».\n\nТехнология явно имеет большие перспективы. И как бы не возмущались правозащитники, и даже если Google пока запрещает подобные приложения для Google Glass, но технический прогресс остановить невозможно. И если быстрое сканирование миллионов фотографий станет технически возможным, то можно не сомневаться, что подобные приложения станут популярными. Тем более, что программы распознавания лиц уже вовсю работают в казино и многих магазинах для автоматического распознавания известных мошенников и воришек на входе в заведение.\n\nВ данный момент вышла закрытая бета-версия NameTag для Google Glass. До конца I кв. 2014 года разработчики обещают выпустить версии для Android и iOS. "
  val splitted = Lexer.split(testText.toLowerCase())
  val filtered = splitted.map(t => if (StopWordsFilter.getList.contains(t)) dummy else t)

  println(filtered.mkString(" "))

  val tokens =
    for {
      word <- filtered
      if !StopWordsFilter.getList.contains(word)
      tokenized <- Tokenizer.tokenize(word).headOption
      if !StopWordsFilter.getList.contains(tokenized)
    } yield {
      if (Vocabulary.normalizedWords.contains(tokenized)) tokenized
      else dummy
    }

  val duples = tokens.zip(tokens.tail)
  val triples = duples.zip(tokens.tail.tail)
  val tetrics = triples.zip(tokens.tail.tail.tail)

  def buildResults(phrases: Iterable[String]): Iterable[(String, Double)] = {
    phrases.map {
      phrase => {
        val kp = Future(KeyphrasenessCalculator.getKeyPhraseness(phrase))
        val res =
          Try {
            Await.result(kp, 4 seconds)
          } match {
            case Failure(e) => {
              log.info("Takes too long: %s".format(phrase))
              None
            }
            case Success(kps) => {
              if (kps > 0) {
                log.info(kps + "\t" + phrase)
                Some((phrase, kps))
              }
              else None
            }
          }
        res
      }
    }
  }.flatten.toSet

  //  tokens.foreach(
  //    token => Searcher.tryPhrase(token)
  //  )

  ////  System.exit(-1)
  //  val ress = buildResults(tokens)
  //
  //  log.info(ress.toSeq.sortBy(-_._2).take(50).mkString("\n"))
  //  Thread.sleep(3000)

  val dResults: Iterable[(String, Double)] = {
    buildResults(
      duples.map {
        case (first, second) => "%s %s".format(first, second)
      })
  }

  log.info(dResults.toSeq.sortBy(-_._2).take(50).mkString("\n"))

  val tResults: Iterable[(String, Double)] = {
    buildResults(
      triples.map {
        case ((first, second), third) => "%s %s %s".format(first, second, third)
      })
  }

  log.info(tResults.toSeq.sortBy(-_._2).take(50).mkString("\n"))

  val ttResults: Iterable[(String, Double)] = {
    buildResults(
      tetrics.map {
        case (((first, second), third), fourth) =>
          "%s %s %s %s".format(first, second, third, fourth)
      })
  }

  log.info(ttResults.toSeq.sortBy(-_._2).take(50).mkString("\n"))
}
