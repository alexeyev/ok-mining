package ru.stachek66.okminer.ner

import ru.stachek66.okminer.language.russian.{Lexer, Tokenizer}
import ru.stachek66.okminer.utils.FileUtils
import org.slf4j.LoggerFactory

/**
 * @author alexeyev
 */
object NaiveNER extends App {
  private val log = LoggerFactory.getLogger("testing-ner")

  val tokens =
    Lexer.split(
      FileUtils.asStringWithoutNewLines(
        new java.io.File("test.txt")))

  val triples =
    tokens.zip(tokens.tail.zip(tokens.tail.tail)).
      map {
      case (one, (two, three)) =>
        (one, two, three)
    }

  val duples =
    tokens.zip(tokens.tail)


  log.info("3-token search")

  triples.foreach(
    t => {
      val res = Searcher.find("%s %s %s".format(t._1, t._2, t._3), 2)
      if (res.nonEmpty)
        println(res)
    }
  )

  log.info("2-token search")

  duples.foreach(
    t => {
      val res = Searcher.find("%s %s".format(t._1, t._2), 1)
      if (res.nonEmpty)
        println(t, res)
    }
  )

  log.info("One-token search")

  tokens.foreach(
    t => {
      val res = Searcher.find("%s".format(t), 7f)
      if (res.nonEmpty) {
        println(t, res)
      }
    }
  )
}
