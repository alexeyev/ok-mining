package ru.stachek66.okminer

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.{StopWordsFilter, Lexer, Tokenizer}
import ru.stachek66.okminer.wiki.KeyphrasenessCalculator
import ru.stachek66.okminer.wiki.vocabulary.Vocabulary
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.util.{Success, Failure, Try}

/**
 * @author alexeyev
 */
object FullPipeLineTool extends App {

  private val log = LoggerFactory.getLogger("test-tool")

  import ExecutionContext.Implicits.global

  val testText = "Христианство начало распространяться у готов в первой половине IV века. " +
    "Среди германских племён готы первые[en] приняли христианство. Особенности " +
    "христианизации готов, пришедшейся на разгар арианского спора, исследователи " +
    "связывают с различными внешними и внутренними факторами. В силу исторических " +
    "обстоятельств, готы приняли христианство в его арианской форме. Библия, переведённая" +
    " епископом готов Вульфилой на готский язык, использовалась и другими народами." +
    "\n\nХристианство было государственной религией в двух основных государственных " +
    "образованиях готов — в королевстве вестготов, где до 589 года доминировало " +
    "арианство, и в королевстве остготов, где арианство было основной конфессией до " +
    "разрушения этого государства Византией в середине VI века. Сохранилось не очень много" +
    " сведений о том, как была устроена христианская церковь в государствах готов, однако" +
    " имеющаяся информация указывает на то, что христианство не играло определяющей" +
    " роли в жизни готов. Не известно о случаях значимых конфликтов на религиозной почве " +
    "между арианами и сторонниками ортодоксального христианства. Также конфессиональные " +
    "предпочтения готов не были важными факторами в их отношениях с другими германскими " +
    "народами, вне зависимости от того, исповедовали ли они одну религию с готами или нет."

  val splitted = Lexer.split(testText)
  //todo: lemmatization?
//  val filtered = StopWordsFilter.filter(splitted.map(_.toLowerCase()))

  val tokens =
    for {
      word <- splitted
      tokenized <- Tokenizer.tokenize(word).headOption

    } yield {
      if (Vocabulary.normalizedWords.contains(tokenized)) tokenized
      else "dummyword"
    }

  val duples = tokens.zip(tokens.tail)

  //  duples.foreach(d => println(d + "\n"))
  val triples = duples.zip(tokens.tail.tail)
  val tetrics = triples.zip(tokens.tail.tail.tail)
  val pentics = tetrics.zip(tokens.tail.tail.tail.tail)

  val dResults: Iterable[(String, String, Double)] = {
    duples.map {
      case (first, second) => {
        val kp = Future(KeyphrasenessCalculator.getKeyPhraseness("%s %s".format(first, second)))
        val res =
          Try {
            Await.result(kp, 4 seconds)
          } match {
            case Failure(e) => {
              log.warn("Takes too long: %s %s".format(first, second))
              None
            }
            case Success(kps) => {
              if (kps > 0) {
                log.info(kps + "\t" + first + " " + second)
                Some((first, second, kps))
              }
              else None
            }
          }
        res
      }
    }
  }.flatten

  log.info(dResults.toSeq.sortBy(-_._3).take(5).mkString("\n"))
}
