package ru.stachek66.okminer

import java.io.File
import java.util.concurrent.TimeUnit
import ner.indexing.{LuceneNER, Searcher}
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.ner.NER
import ru.stachek66.okminer.utils.{CounterLogger, FileUtils}
import scala.concurrent._
import scala.concurrent.duration.Duration
import ru.stachek66.okminer.Meta.singleContext
import utils.storage.Dao

/**
 * Processing a single-year folder in corpus.
 * @author alexeyev
 */
private[okminer] class OneYearProcessor(ner: NER = new LuceneNER(new Searcher),
                                        trendsMiner: TrendsTool = new TrendsTool()) {

  type Company = String
  type Trend = String

  private val log = LoggerFactory.getLogger("one-year-processor")
  private val clog = new CounterLogger(log, 5, "%s files processed")

  private def extractFromFile(file: File): Iterable[(Trend, Company)] = {
    val description = FileUtils.asStringWithoutNewLines(file)

    val companies: Set[String] = ner.extractAllCompanies(description)
    val trends: Iterable[(Double, String, String, String)] = trendsMiner.extractTrends(description)

    val allPairs = for {
      (_, _, _, trend) <- trends
      company <- companies
    } yield (trend.trim, company.trim)
    log.info(s"File ${file.getName} :\t${trends.size} trends,\t${companies.size} companies,\tproduct-to-set: ${allPairs.size}")
    allPairs.toSet
  }

  /**
   * Getting all stats from directory
   * @param source target directory
   */
  def extractFromYearDirectory
  (source: File,
   externalCounter: Iterable[CounterLogger] = List.empty[CounterLogger]): Iterable[(Trend, Company, Int)] = {

    log.info("Extracting from dir " + source.getAbsolutePath)

    def safeExtract(files: Iterable[File]): scala.concurrent.Future[Iterable[(Trend, Company)]] =
      scala.concurrent.future {
        (for {file <- files} yield
          try {
            val extracted = extractFromFile(file)
            externalCounter.foreach(_.execute(()))
            extracted
          } catch {
            case e: org.apache.lucene.queryparser.classic.ParseException =>
              log.error("This file seems to be corrupted" + file.getAbsolutePath)
              Iterable.empty[(Trend, Company)]
            case e: Exception =>
              log.error("Problems extracting from file " + file.getAbsolutePath, e)
              Iterable.empty[(Trend, Company)]
          }).flatten
      }

    val cores = Runtime.getRuntime.availableProcessors()
    log.info(s"BTW, $cores cores are available")

    val files =
      if (source.isDirectory)
        source.listFiles().toIterable.
          filter {
          candidate =>
            !candidate.isDirectory && candidate.length() < 100 * 1000 * 1000
        }
      else Iterable[File]()

    val groupedFiles = files.grouped(files.size / cores)

    Await.result(
      Future.sequence(
        groupedFiles.map(safeExtract(_))
      ),
      Duration(2, TimeUnit.DAYS)
    ).flatten.toIterable.
      groupBy {
      case (trend, company) => (trend, company)
    } map {
      case ((trend, company), pairs) => (trend, company, pairs.size)
    }
  }

  /**
   * Writing all results to a local stored bag for further processing and flushing to .tsv files
   */
  def flushFromYearDirectory(source: File, externalCounter: Iterable[CounterLogger], dao: Dao) {

    log.info("Extracting from dir " + source.getAbsolutePath)

    def safeExtract(file: File): scala.concurrent.Future[Unit] =
      scala.concurrent.future {
        try {
          val extracted = extractFromFile(file)
          externalCounter.foreach(_.execute(()))
          val year = source.getName.toInt
          dao.put(
            extracted.map {
              case (trend, company) => (year, trend, company)
            } toIterable
          )
        } catch {
          case e: org.apache.lucene.queryparser.classic.ParseException =>
            log.error("This file seems to be corrupted" + file.getAbsolutePath)
            Iterable.empty[(Trend, Company)]
          case e: Exception =>
            log.error("Problems extracting from file " + file.getAbsolutePath, e)
            Iterable.empty[(Trend, Company)]
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
    )
  }
}