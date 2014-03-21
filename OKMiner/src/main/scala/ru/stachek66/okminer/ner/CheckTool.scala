package ru.stachek66.okminer.ner

import java.io.File
import ru.stachek66.okminer.utils.FileUtils

/**
 * @author alexeyev
 */
private object CheckTool extends App {

  val description = FileUtils.asString(new File("test.txt"))
  val companies = NaiveNER.extractAllCompanies(description)
  companies.foreach(println)

}
