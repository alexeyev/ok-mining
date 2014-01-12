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

  val duples =
    tokens.zip(tokens.tail).
      filterNot(
      t =>
        HeuristicsHelper.isDigits(t._1) &&
          HeuristicsHelper.isDigits(t._2))

  log.info("2-token search")

  val companiesFromDuples = {
    for {
      d <- duples
      res = Searcher.fuzzyFind("%s %s".format(d._1, d._2), 1)
      if (res.nonEmpty)
    } yield {
      log.info(d + " " + res.head)
      res.head.getField(Searcher.companyField).stringValue()
    }
  } toSet

  log.info("One-token search")

  val companiesFromTokens = {
    for {
      t <- tokens
      res = Searcher.magicFind("%s".format(t), 7f)
      if res.nonEmpty
    } yield {
      log.info(res.toString())
      res.head._2.getField(Searcher.companyField).stringValue()
    }
  } toSet

  println(companiesFromDuples ++ companiesFromTokens)
}