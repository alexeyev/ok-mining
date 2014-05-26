package ixbt

import java.io.FileWriter
import org.jsoup.Jsoup

/**
 * Created by alexeyev on 26.05.14.
 */
object IxbtLinksParser extends App {

  val fw = new FileWriter("../ixbt/raw/hard_links.tsv")

  for (year <- new java.io.File("../ixbt/raw/hard").listFiles()) {
    println("lol year " + year.toString)
    for (file <- year.listFiles()) {
      val text = io.Source.fromFile(file)("WINDOWS-1251").getLines().mkString("\n")
      val doc = Jsoup.parse(text)
      val els = doc.select("a.nl_nw_link")
      val iterator = els.iterator()
      while (iterator.hasNext) {
        fw.write(year.getName + "\thttp://www.ixbt.com" + iterator.next().attr("href") + "\n")
      }
    }
  }

  fw.close()
}
