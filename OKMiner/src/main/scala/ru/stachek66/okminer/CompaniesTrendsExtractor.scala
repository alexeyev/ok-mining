package ru.stachek66.okminer

import java.io.File
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


  def main(args: Array[String]) {
    if (args.size < 2) {
      log.error("Please provide 2 arguments: source and destination; %d args provided".format(args.size))
    } else {
      val source = new File(args(0))
      val destination = new File(args(1))
      if (!source.exists()) {
        log.error("Please provide existing source file/directory.")
      } else {
        val extracted = (
          if (source.isDirectory) source.listFiles().toIterable
          else Iterable(source)
          ) flatMap {
          clog.tick()
          extractFromFile(_)
        } groupBy {
          case (trend, company, count) => (trend, company)
        } map {
          case ((trend, company), triples) => (trend, company, triples.map(_._3).sum)
        }
        destination.getParentFile.mkdirs()
        StatsFileIO.writeToFile(extracted, destination)
      }
    }
  }
}

object DefaultRunner extends App {
  for {
    year <- 1995 to 2014
  } Try {
    CompaniesTrendsExtractor.main(Array("./corpus-media/clean/%s/".format(year), "results/%s.tsv".format(year)))
  }
}
