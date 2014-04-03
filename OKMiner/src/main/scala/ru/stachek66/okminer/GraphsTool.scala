package ru.stachek66.okminer

import java.io.File
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.utils.{Conversions, StatsFileIO}
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

    for {
      file <- {
        val files = src.listFiles()
        log.info(s"Dir $src contains ${files.size} files.")
        files
      }
      (triples, year) = StatsFileIO.readFromFile(file)
      (trend, company, count) <- triples
    } {
      init(trend, company)
      val map = megamap(trend)(company)
      map.put(year, count)
      megamap(trend).put(company, map)
    }

    //filter
    val filteredMegaMap = for {
      (trend, companiesMap) <- megamap
      if companiesMap.exists {
        case (company, yearMap) =>
          drawConfig.yearsAppropriate(yearMap.toMap.keySet)
      }
      newCompaniesMap = companiesMap.filter {
        case (company, yearMap) =>
          drawConfig.sufficientNumberOfYears(yearMap.toMap.keySet)
      }
    } yield trend -> newCompaniesMap

    val cores = Runtime.getRuntime.availableProcessors()
    log.info("Available cores: " + cores)

    val models = filteredMegaMap.map {
      case (trend, map) => Model(trend, Conversions.toImmutable(map))
    }

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