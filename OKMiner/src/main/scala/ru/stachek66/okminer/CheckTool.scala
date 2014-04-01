package ru.stachek66.okminer

import java.io.File
import utils.FileUtils
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * @author alexeyev
 */
object CheckTool extends App {

  val start = new Date()
  CorpusProcessorWriter.processCorpus(new File("../corpus-test/clean"), new File("../corpus-test"))
  //  val trendsMiner = new TrendsTool()
  //  val ext = trendsMiner.extractTrends(FileUtils.asStringWithoutNewLines(new File("test/test.txt")))
  val end = new Date()

  println(TimeUnit.SECONDS.convert(end.getTime - start.getTime, TimeUnit.MILLISECONDS) + " seconds")
//  println(ext.mkString("\n"))
}