package ru.stachek66.okminer.wiki.translation.tools

import java.io.{File, FileWriter}
import java.util.Date
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.Meta
import ru.stachek66.okminer.Meta.singleContext
import scala.concurrent._

/**
 * Ru-En links acquisition tool.
 * @author alexeyev
 */
object Tool extends App {

  private val log = LoggerFactory.getLogger("translation-data-collection-logger")

  new File("parsed").mkdirs()
  private val fw = new FileWriter("parsed/ru-en-titles.tsv")
  val start = new Date()

  val ruToEnResult = {//future {

    val ruIdToEnTitle = LangLinksSQLParser.idToEnTitle

    println(ruIdToEnTitle)

    PageSQLParser.parseDump(PageSQLParser.rudump, "utf8") {
      (id, title) => {
        if (ruIdToEnTitle.keySet.contains(id)) {
          //synchronized {
            fw.write("%s\t%s\n".format(title, ruIdToEnTitle(id)))
          //}
        }
      }
    }
  }

  val enToRuResult = {

    val enIdToRuTitle = LangLinksSQLParser.idToRuTitle

    PageSQLParser.parseDump(PageSQLParser.endump, "utf8") {
      (id, title) => {
        if (enIdToRuTitle.keySet.contains(id)) {//synchronized {
          val ruTitle = enIdToRuTitle(id).replaceAll("Категория:", "").replaceAll("Шаблон:", "")
          fw.write("%s\t%s\n".format(ruTitle, title))
        }
      }
    }
  }

//  Await.result(ruToEnResult, Meta.maxDuration)
  fw.close()

  log.info("Sorting...")

  val sortedFW = new FileWriter("parsed/ru-en-sorted.tsv")
  io.Source.fromFile(new File("parsed/ru-en-titles.tsv"))("UTF-8").getLines().toSet[String].foreach {
    case line => sortedFW.write(line.trim + "\n")
  }
  sortedFW.close()

  new File("parsed/ru-en-titles.tsv").delete()

  val stop = new Date()

  log.info(s"Done in ${TimeUnit.SECONDS.convert(stop.getTime - start.getTime, TimeUnit.MILLISECONDS)} seconds")
}