package ru.stachek66.okminer

import java.io.FileWriter
import ru.stachek66.okminer.utils.FileUtils

/**
 * @author alexeyev
 */
object EvalPrepTool2 extends App {

  val dir = new java.io.File("../corpus-test/clean/2014/")

  println(dir.isDirectory)

  val fw = new FileWriter("../../Desktop/trends-eval.tsv")

  var counter = 0

  for (file <- dir.listFiles()) {
    val text: String = FileUtils.asStringWithoutNewLines(file)
    if (counter <= 100 && text.size < 2000 && text.size > 50) {
      counter += 1
      val trends = new TrendsTool().extractTrends(text).map(_._4).toSet
      fw.write(text + "\t" + trends.mkString(",") + "\n")
    }
  }
  fw.close()
}
