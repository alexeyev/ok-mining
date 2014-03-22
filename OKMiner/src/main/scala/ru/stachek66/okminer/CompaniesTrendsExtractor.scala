package ru.stachek66.okminer

import java.io.File
import java.util.Date
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.ner.NaiveNER
import ru.stachek66.okminer.utils.{StatsFileIO, CounterLogger, FileUtils}
import scala.util.Try

/**
 * @author alexeyev
 */
object CompaniesTrendsExtractor {

  type Company = String
  type Trend = String

  private val log = LoggerFactory.getLogger("companies-trends-extractor")
  private val clog = new CounterLogger(log, 5, "%s files processed")

  private def extractFromFile(file: File): Iterable[(Trend, Company, Int)] = {
    log.debug(file.getName)
    val description = FileUtils.asStringWithoutNewLines(file)
    val trends = TrendsTool.extractTrends(description)
    val companies = NaiveNER.extractAllCompanies(description)
    val allPairs = for {
      trend <- trends
      company <- companies
    } yield (trend.trim, company.trim)
    allPairs.groupBy(pair => pair).map {
      case ((trend, company), group) => (trend, company, group.size)
    }
  }

  private def extractFromYearDirectory(source: File): Iterable[(Trend, Company, Int)] = {
    (
      if (source.isDirectory) source.listFiles().toIterable
      else Iterable(source)
      ) flatMap {
      clog.execute {
        extractFromFile(_)
      }
    } groupBy {
      case (trend, company, count) => (trend, company)
    } map {
      case ((trend, company), triples) => (trend, company, triples.map(_._3).sum)
    }
  }

  def main(args: Array[String]) {
    if (args.size < 2) {
      log.error("Please provide 2 arguments: source and destination; %d args provided".format(args.size))
    } else {
      val source = new File(args(0))
      val destination = new File(args(1))
      if (!source.exists()) {
        log.error("Please provide existing source file/directory.")
      } else {
        val extracted = extractFromYearDirectory(source)
        destination.getParentFile.mkdirs()
        StatsFileIO.writeToFile(extracted, destination)
      }
    }
  }
}

object DefaultRunner extends App {

  private val categories = List("media", "science")

  for {
    category <- categories
    year <- 1999 to 2014
  } Try {
    val start = new Date()
    CompaniesTrendsExtractor.main(Array(s"../corpus-$category/clean/$year/", s"../corpus-$category/results/$year.tsv"))
    val end = new Date()
    val elapsed = TimeUnit.SECONDS.convert(end.getTime - start.getTime, TimeUnit.MILLISECONDS)
    println(s"Done in $elapsed seconds.")
  }
}
