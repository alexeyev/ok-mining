package ru.stachek66.okminer

import java.io.File
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.ner.{NER, Searcher, NaiveNER}
import ru.stachek66.okminer.utils.{CounterLogger, FileUtils}
import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.util.{Failure, Try, Success}
import ru.stachek66.okminer.Meta.singleContext

/**
 * @author alexeyev
 */
private[okminer] class OneYearProcessor(ner: NER = new NaiveNER(new Searcher),
                                        trendsMiner: TrendsTool = new TrendsTool()) {

  type Company = String
  type Trend = String

  private val log = LoggerFactory.getLogger("one-year-processor")
  private val clog = new CounterLogger(log, 5, "%s files processed")

  private def extractFromFile(file: File): Iterable[(Trend, Company, Int)] = {
    log.debug(file.getName)
    val description = FileUtils.asStringWithoutNewLines(file)
//    val fTrends = future(trendsMiner.extractTrends(description)) //(ru.stachek66.okminer.Meta.singleContext)
//    val fCompanies = future(ner.extractAllCompanies(description)) //(ru.stachek66.okminer.Meta.singleContext)
//    val List(trends, companies) =
//      Await.result(
//        Future.sequence(List(fTrends, fCompanies)),
//        Duration(12, TimeUnit.HOURS))
    val trends = trendsMiner.extractTrends(description)
    val companies = ner.extractAllCompanies(description)
    val allPairs = for {
      trend <- trends
      company <- companies
    } yield (trend.trim, company.trim)
    allPairs.groupBy(pair => pair).map {
      case ((trend, company), group) => (trend, company, group.size)
    }
  }

  def extractFromYearDirectory(source: File, externalCounter: Option[CounterLogger] = None): Iterable[(Trend, Company, Int)] = {

    def safeExtract(file: File): scala.concurrent.Future[Iterable[(Trend, Company, Int)]] =
      scala.concurrent.future {
        Try {
          val extracted = extractFromFile(file)
          externalCounter.foreach(_.execute(()))
          extracted
        } match {
          case Failure(e: org.apache.lucene.queryparser.classic.ParseException) =>
            log.error("This file seems to be corrupted" + file.getAbsolutePath)
            Iterable.empty[(Trend, Company, Int)]
          case Failure(e) =>
            log.error("Problems extracting from file " + file.getAbsolutePath, e)
            Iterable.empty[(Trend, Company, Int)]
          case Success(triple) => triple
        }
      }

    val files =
      if (source.isDirectory) source.listFiles().toIterable
      else Iterable[File]()

    Await.result(
      Future.sequence(
        files.map(safeExtract(_))
      ),
      Duration(2, TimeUnit.DAYS)
    )
      .flatten.
      groupBy {
      case (trend, company, count) => (trend, company)
    } map {
      case ((trend, company), triples) => (trend, company, triples.map(_._3).sum)
    }
  }


  //    def main(args: Array[String]) {
  //      if (args.size < 2) {
  //        log.error("Please provide 2 arguments: source and destination; %d args provided".format(args.size))
  //      } else {
  //        val source = new File(args(0))
  //        val destination = new File(args(1))
  //        if (!source.exists()) {
  //          log.error("Please provide existing source file/directory.")
  //        } else {
  //          val extracted = extractFromYearDirectory(source)
  //          destination.getParentFile.mkdirs()
  //          StatsFileIO.writeToFile(extracted, destination)
  //        }
  //      }
  //    }
}

private object DefaultRunner extends App {

  private val categories = List("media", "science")

    val f1 = future {
      Thread.sleep(10000)
      1
    } (ru.stachek66.okminer.Meta.singleContext)

  List(f1,f1,f1).foreach(f =>
    println(Await.result(f, Duration(12, TimeUnit.SECONDS))))
  //
  //  val f2 = future {
  //    Thread.sleep(20000)
  //    2
  //  } (ru.stachek66.okminer.Meta.singleContext)
  //
  //  val a = for {
  //    f <- f1
  //  } yield f
  //
  //  print(a)


  //  for {
  //    category <- categories
  //    year <- 1999 to 2014
  //  } Try {
  //    val start = new Date()
  //    //    CompaniesTrendsExtractor.main(Array(s"../corpus-$category/clean/$year/", s"../corpus-$category/results/$year.tsv"))
  //    val end = new Date()
  //    val elapsed = TimeUnit.SECONDS.convert(end.getTime - start.getTime, TimeUnit.MILLISECONDS)
  //    println(s"Done in $elapsed seconds.")
  //  }
}
