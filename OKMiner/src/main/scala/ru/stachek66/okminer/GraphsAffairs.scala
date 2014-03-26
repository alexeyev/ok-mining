package ru.stachek66.okminer

import java.io.File
import java.util.Date
import java.util.concurrent.TimeUnit
import ru.stachek66.okminer.utils.{Conversions, StatsFileIO}
import ru.stachek66.okminer.visualization.{Config, ChartPrinter, ChartGenerator, Model}
import scala.collection.mutable.{Map => mMap}
import org.slf4j.LoggerFactory

/**
 * Drawing graphs by reports.
 * @author alexeyev
 */
class GraphsTool(drawConfig: Config = Config()) {

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
      file <- src.listFiles()
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
    } yield trend -> companiesMap

    filteredMegaMap.map {
      case (trend, map) => Model(trend, Conversions.toImmutable(map))
    } foreach {
      model => {
        val chart = ChartGenerator.buildChart(model)
        ChartPrinter.print(chart, new File(dest.getAbsolutePath + "/" + model.trend.replaceAll("\\s", "_") + ".png"))
      }
    }
  }
}

private object GraphDrawingTool extends App {

  private val categories = List("media", "science")
//
//  {
//    for (category <- categories) {
//      val start = new Date()
////      GraphsTool.drawFromDirectory(new File(s"../corpus-$category/results"), new File(s"../corpus-$category/graphs"))
//      val end = new Date()
//      val elapsed = TimeUnit.MINUTES.convert(end.getTime - start.getTime, TimeUnit.MILLISECONDS)
//      println(s"Done in $elapsed seconds.")
//    }
//  }

}