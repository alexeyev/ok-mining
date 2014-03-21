package ru.stachek66.okminer

import java.io.File
import ru.stachek66.okminer.utils.{Conversions, StatsFileIO}
import ru.stachek66.okminer.visualization.{ChartPrinter, ChartGenerator, Model}
import scala.collection.mutable.{Map => mMap}

/**
 * An inst
 * @author alexeyev
 */
object GraphsTool {

  def drawFromDirectory(directory: File, dest: File) {

    directory.mkdirs
    dest.mkdirs

    //    require(directory.isDirectory)
    //    require(dest.isDirectory)

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
      file <- directory.listFiles()
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

object GraphsRunner extends App {

//  GraphsTool.drawFromDirectory(new File("test-corpus/results"), new File("test-corpus/graphs"))

    GraphsTool.drawFromDirectory(new File("corpus-media/results"), new File("corpus-media/graphs"))

}