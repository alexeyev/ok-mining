package ru.stachek66.okminer.ner

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.{StopWordsFilter, Lexer}
import ru.stachek66.okminer.utils.FileUtils

/**
 * NER tool. To extract all known companies from a text,
 * call "extractAllCompanies".
 * @author alexeyev
 */
object NaiveNER {

  private val log = LoggerFactory.getLogger("testing-ner")

  def extractAllCompanies(sourceText: String) = {

    val tokens =
      StopWordsFilter.filter(
        Lexer.split(
          HeuristicsHelper.replaceUrls(sourceText)))

    val duples =
      tokens.zip(if (tokens.isEmpty) Seq() else tokens.tail).
        filterNot {
        case (token1, token2) =>
          HeuristicsHelper.isDigits(token1) && HeuristicsHelper.isDigits(token2)
      }

    log.debug("2-token search")

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

    log.debug("1-token search")

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

    companiesFromDuples ++ companiesFromTokens
  }
}