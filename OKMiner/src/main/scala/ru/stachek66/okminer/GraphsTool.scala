package ru.stachek66.okminer

import java.io.File
import org.slf4j.LoggerFactory
import utils.{CounterLogger, Conversions, StatsFileIO}
import ru.stachek66.okminer.visualization.{DrawingConfig, ChartPrinter, ChartGenerator, Model}
import scala.collection.mutable.{Map => mMap}
import ru.stachek66.okminer.Meta.singleContext
import concurrent.{Future, Await}
import java.util.concurrent.TimeUnit
import concurrent.duration.Duration

/**
 * Drawing graphs by reports.
 * @author alexeyev
 */
class GraphsTool(drawConfig: DrawingConfig = DrawingConfig()) {

  private val log = LoggerFactory.getLogger("graphs-drawing")
  private val clog = new CounterLogger(log, 5, "%s charts formed")

  /**
   * Drawing graphs by *.tsv reports from one directory and storing them at another one
   * @param src source directory
   * @param dest destination directory
   */
  def drawFromDirectory(src: File, dest: File) {

    log.info(s"From $src to $dest")

    src.mkdirs
    dest.mkdirs

    val megamap = mMap[String, mMap[String, mMap[Int, Float]]]()

    def init(trend: String, company: String) {
      if (!megamap.contains(trend)) {
        megamap.put(trend, mMap[String, mMap[Int, Float]]())
      }
      if (!megamap(trend).contains(company)) {
        megamap(trend).put(company, mMap[Int, Float]())
      }
    }

    log.info("Building...")

    val years = collection.mutable.ArrayBuffer.empty[Int]
    for {
      file <- {
        val files = src.listFiles()
        log.info(s"Dir $src contains ${files.size} files.")
        files.filter(_.isFile)
      }
      (triples, year) = {
        val (t, y) = StatsFileIO.readFromFile(file)
        years += y
        (t, y)
      }
      (trend, company, count) <- triples
    } {
      init(trend, company)
      val map = megamap(trend)(company)
      map.put(year, count)
      megamap(trend).put(company, map)
    }

    log.info("Adding zeroes...")

    // adding zeroes
    for {
      year <- years.min to years.max
      (trend, compMap) <- megamap
      (comp, yearMap) <- compMap
      if !yearMap.keySet.contains(year)
    } yearMap.put(year, 0)

    log.info("Filtering...")

    //filter
    val models = for {
      (trend, companiesMap) <- megamap
      if companiesMap.exists {
        case (company, yearMap) =>
          drawConfig.yearsAppropriate(yearMap.toMap)
      }
      newCompaniesMap = companiesMap.filter {
        case (company, yearMap) =>
          drawConfig.sufficientNumberOfYears(yearMap.toMap)
      }
    } yield Model(trend, newCompaniesMap)


    val cores = Runtime.getRuntime.availableProcessors()

    log.info("Available cores: " + cores + ". Printing...")

    if (models.size != 0) {
      val tasks =
        for (subSet <- models.grouped(models.size / cores + 1))
        yield scala.concurrent.future {
          subSet.foreach {
            model => {
              val chart = ChartGenerator.buildChart(model)
              ChartPrinter.print(chart, new File(dest.getAbsolutePath + "/" + model.trend.replaceAll("[\\s\\.\\(\\)]", "_") + ".png"))
            }
          }
        }
      Await.result(Future.sequence(tasks), Duration(2, TimeUnit.DAYS))
    }
  }
}