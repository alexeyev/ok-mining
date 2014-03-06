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

  def extractAllCompanies(sourceText: String) = {

    val tokens =
      StopWordsFilter.filter(
        Lexer.split(
          HeuristicsHelper.replaceUrls(sourceText)))

    val duples =
      tokens.zip(if (tokens.isEmpty) Seq() else tokens.tail).
        filterNot(
        t =>
          HeuristicsHelper.isDigits(t._1) && HeuristicsHelper.isDigits(t._2))

    log.info("2-token search")

    val companiesFromDuples = {
      for {
        d <- duples
        res = Searcher.magicFind("%s %s".format(d._1, d._2), 5f)
        if res.nonEmpty
      } yield {
        log.debug(d + " ~> " + res.head)
        res.head._2.getField(Searcher.companyField).stringValue()
      }
    } toSet

    log.info("1-token search")

    val companiesFromTokens = {
      for {
        t <- tokens
        res = Searcher.magicFind("%s".format(t), 8f)
        if res.nonEmpty
      } yield {
        log.debug(t + " -> " + res.toString())
        res.head._2.getField(Searcher.companyField).stringValue()
      }
    } toSet

    (companiesFromDuples ++ companiesFromTokens)
  }
}