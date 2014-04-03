package ru.stachek66.okminer.ner.indexing

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.{StopWordsFilter, Lexer}
import ru.stachek66.okminer.ner.{HeuristicsHelper, NER}

/**
 * NER Lucene tool. To extract all known companies from a text,
 * call "extractAllCompanies".
 * @author alexeyev
 */
class LuceneNER(searcher: Searcher) extends NER {

  private val log = LoggerFactory.getLogger("ner")

  /**
   * @param sourceText text in Russian language
   * @return a list of extracted organizations
   */
  def extractAllCompanies(sourceText: String): Set[String] = {

    val tokens =
      StopWordsFilter.filter(
        Lexer.split(
          HeuristicsHelper.replaceCommas(
            HeuristicsHelper.replaceUrls(sourceText))))

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
        res = searcher.strictFind("%s %s".format(d._1, d._2))
        if res.nonEmpty
      } yield {
        log.debug(d + " ~> " + res.head)
        res.head._2.getField(IndexProperties.companyField).stringValue()
      }
    } toSet

    log.debug("1-token search")

    val companiesFromTokens = {
      for {
        t <- tokens
        res = searcher.strictFind("%s".format(t))
        if res.nonEmpty
      } yield {
        log.debug(t + " -> " + res.toString())
        res.head._2.getField(IndexProperties.companyField).stringValue()
      }
    } toSet

    log.debug("" + companiesFromDuples + " " + companiesFromTokens)

    companiesFromDuples ++ companiesFromTokens
  }
}