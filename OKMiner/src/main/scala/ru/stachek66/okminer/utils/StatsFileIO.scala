package ru.stachek66.okminer.utils

import java.io.{FileWriter, File}
import org.slf4j.LoggerFactory

/**
 * Reading and writing specific tuples to tsv file.
 * company \t trend \t count \n
 * @author alexeyev
 */
object StatsFileIO {

  type Trend = String
  type Company = String
  type Year = Int

  private val log = LoggerFactory.getLogger("tsv-converter")

  def writeToFile(data: Iterable[(Trend, Company, Int)], file: File) {
    val fw = new FileWriter(file)
    data.toSeq.sortBy(_._2).foreach {
      case (trend, company, count) => {
        val line = "%s\t%s\t%s".format(company, trend, count)
        log.info(line)
        fw.write(line + "\n")
      }
    }
    fw.close()
  }

  def readFromFile(file: File): (Iterable[(Trend, Company, Int)], Year) = {
    val year = file.getName.replaceAll("\\.tsv", "").toInt

    val triples = for (line <- io.Source.fromFile(file).getLines().toIterable) yield {
      val splitted = line.trim.split("\t")
      (splitted(1), splitted(0), splitted(2).toInt)
    }
    (triples,year)
  }
}

object tool extends App {

  println(StatsFileIO.readFromFile(new File("results/2002.tsv")))
}