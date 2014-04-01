package ru.stachek66.okminer

import java.io.File
import java.util.concurrent.TimeUnit
import ner.indexing.{LuceneNER, Searcher}
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.ner.{NER}
import ru.stachek66.okminer.utils.{CounterLogger, FileUtils}
import scala.concurrent._
import scala.concurrent.duration.Duration
import ru.stachek66.okminer.Meta.singleContext

/**
 * @author alexeyev
 */
private[okminer] class OneYearProcessor(ner: NER = new LuceneNER(new Searcher),
                                        trendsMiner: TrendsTool = new TrendsTool()) {

  type Company = String
  type Trend = String

  private val log = LoggerFactory.getLogger("one-year-processor")
  private val clog = new CounterLogger(log, 5, "%s files processed")

  private def extractFromFile(file: File): Iterable[(Trend, Company, Int)] = {
    val description = FileUtils.asStringWithoutNewLines(file)
//        val fTrends = future(trendsMiner.extractTrends(description)) //(ru.stachek66.okminer.Meta.singleContext)
//        val fCompanies = future(ner.extractAllCompanies(description)) //(ru.stachek66.okminer.Meta.singleContext)

    val trends: Iterable[(Double, String, String, String)] =
//      Await.result(fTrends, Duration(12, TimeUnit.HOURS))
          trendsMiner.extractTrends(description)
    val companies: Set[String] =
//      Await.result(fCompanies, Duration(12, TimeUnit.HOURS))
          ner.extractAllCompanies(description)

    val allPairs = for {
      (_, _, _, trend) <- trends
      company <- companies
    } yield (trend.trim, company.trim)
    log.info(s"File ${file.getName} : ${trends.size} trends, ${companies.size} companies")
    allPairs.groupBy(pair => pair).map {
      case ((trend, company), group) => (trend, company, group.size)
    }
  }

  def extractFromYearDirectory
  (source: File,
   externalCounter: Iterable[CounterLogger] = List.empty[CounterLogger]): Iterable[(Trend, Company, Int)] = {

    log.info("Extracting from dir " + source.getAbsolutePath)

    def safeExtract(file: File): scala.concurrent.Future[Iterable[(Trend, Company, Int)]] =
      scala.concurrent.future {
        try {
          val extracted = extractFromFile(file)
          externalCounter.foreach(_.execute(()))
          extracted
        } catch {
          case e: org.apache.lucene.queryparser.classic.ParseException =>
            log.error("This file seems to be corrupted" + file.getAbsolutePath)
            Iterable.empty[(Trend, Company, Int)]
          case e: Exception =>
            log.error("Problems extracting from file " + file.getAbsolutePath, e)
            Iterable.empty[(Trend, Company, Int)]
        }
      }

    val files =
      if (source.isDirectory) source.listFiles().toIterable
      else Iterable[File]()

    Await.result(
      Future.sequence(
        files.
          filter {
          candidate =>
            !candidate.isDirectory && candidate.length() < 100 * 1000 * 1000
        } map (safeExtract(_))
      ),
      Duration(2, TimeUnit.DAYS)
    ).flatten.
      groupBy {
      case (trend, company, count) => (trend, company)
    } map {
      case ((trend, company), triples) => (trend, company, triples.map(_._3).sum)
    }
  }
}