package ru.stachek66.okminer

import categories.TechCategories
import java.io.File
import utils.FileUtils

/**
 * @author alexeyev
 */
object CheckTool extends App {

  val trendsMiner = new TrendsTool()
  val ext = trendsMiner.extractTrends(FileUtils.asStringWithoutNewLines(new File("test/test.txt")))

  println(ext)

  val fext = ext.filter {
    topic => {
      TechCategories.acceptableTopics.contains(topic)
    }
  }

  println(fext)
}
