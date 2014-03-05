package ru.stachek66.okminer.ner

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.{StopWordsFilter, Lexer}
import ru.stachek66.okminer.utils.FileUtils

/**
 * @author alexeyev
 */
object NaiveNER {
  //todo: rewrite so as mail.ru could be found
  private val log = LoggerFactory.getLogger("testing-ner")

  def main(args: Array[String]) {
    extractAllCompanies(
      FileUtils.asStringWithoutNewLines(new java.io.File("test.txt")))
  }

  def extractAllCompanies(sourceText: String) = {

    val tokens = StopWordsFilter.filter(Lexer.split(sourceText))

    val duples =
      tokens.zip(if (tokens.isEmpty) Seq() else tokens.tail).
        filterNot(
        t =>
          HeuristicsHelper.isDigits(t._1) && HeuristicsHelper.isDigits(t._2))

    log.debug("2-token search")

    val companiesFromDuples = {
      for {
        d <- duples
        res = Searcher.magicFind("%s %s".format(d._1, d._2), 2)
        if res.nonEmpty
      } yield {
        log.debug(d + " ~> " + res.head)
        res.head._2.getField(Searcher.companyField).stringValue()
      }
    } toSet

    log.debug("1-token search")

    val companiesFromTokens = {
      for {
        t <- tokens
        res = Searcher.magicFind("%s".format(t), 6f)
        if res.nonEmpty
      } yield {
        log.debug(t + " -> " + res.toString())
        res.head._2.getField(Searcher.companyField).stringValue()
      }
    } toSet

    (companiesFromDuples ++ companiesFromTokens)
  }
}