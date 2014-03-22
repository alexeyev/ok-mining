package ru.stachek66.okminer

import java.io.File
import ru.stachek66.okminer.utils.{Conversions, StatsFileIO}
import ru.stachek66.okminer.visualization.{ChartPrinter, ChartGenerator, Model}
import scala.collection.mutable.{Map => mMap}

/**
 * Drawing graphs by reports.
 * @author alexeyev
 */
object GraphsTool {

  /**
   * Drawing graphs by *.tsv reports from one directory and storing them at another one
   * @param src source directory
   * @param dest destination directory
   */
  def drawFromDirectory(src: File, dest: File) {

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

    megamap.map {
      case (trend, map) => Model(trend, Conversions.toImmutable(map))
    } foreach {
      model => {
        val chart = ChartGenerator.buildChart(model)
        ChartPrinter.print(chart, new File(dest.getAbsolutePath + "/" + model.trend.replaceAll("\\s", "_") + ".png"))
      }
    }
  }
}