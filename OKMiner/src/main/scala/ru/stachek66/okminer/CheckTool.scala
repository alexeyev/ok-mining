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

  def norm(name: String) = {
    name.replaceAll("Category:", "").toLowerCase.trim
  }

  val fext = ext.map(norm(_)).filter {
    topic => {
      TechCategories.acceptableTopics.contains(topic)
    }
  }

  println(fext)
}
