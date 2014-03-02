package ru.stachek66.okminer.wiki.translation.tools

import java.io._
import java.util.zip.GZIPInputStream
import org.slf4j.LoggerFactory
import scala.util.Failure
import scala.util.Success
import scala.util.{Success, Failure, Try}

/**
 * Parses id-to-title mapping for Russian wiki-articles
 * @author alexeyev
 */
object PageSQLParser {

  private val log = LoggerFactory.getLogger("")

  private[translation] val rudump = new File("../ruwiki-latest-page.sql.gz")
  private[translation] val endump = new File("../enwiki-latest-page.sql.gz")

  private val ruPrepared = new File("parsed/ru-id-title.tsv")
  private val enPrepared = new File("parsed/en-id-title.tsv")

  private val pattern = "\\((\\d+),\\d+,'([^']+)'".r

  private[translation] def parseDump(dump: File, enc: String)(handler: (Long, String) => Unit) {

    // due to malformed input, we have to use buffered reader  :(
    val br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(dump))))
    var c = 0

    while (br.ready()) Try {
      c += 1
      val line = br.readLine()
      if (line.contains("INSERT")) {
        pattern.findAllIn(line).matchData.foreach(
          m => {
            val spl = m.group(1).split(",")
            val id = spl(0).toLong
            val tal = m.group(2).replace("_", " ")
            handler(id, tal)
          })
      }
    } match {
      case Failure(e) => log.error("", e)
      case Success(s) => log.info("Line " + c)
    }
  }

  private def getMapsFromTsv(file: File): (Map[Long, String], Map[String, Long]) = {

    val itt = collection.mutable.Map[Long, String]()
    val tti = collection.mutable.Map[String, Long]()

    io.Source.fromFile(file).getLines().foreach {
      line => {
        val splitted = line.trim.split("\t")
        val number = splitted(0).toLong
        itt.put(number, splitted(1))
        tti.put(splitted(1), number)
      }
    }

    (itt.toMap, tti.toMap)
  }

  lazy val (ruIdToTitle, ruTitleToId): (Map[Long, String], Map[String, Long]) = {
    log.info("Accessing ru map")
    if (!ruPrepared.exists()) {
      flushRu()
    }
    getMapsFromTsv(ruPrepared)
  }

  lazy val (enIdToTitle, enTitleToId): (Map[Long, String], Map[String, Long]) = {
    log.info("Accessing en map")
    if (!enPrepared.exists()) {
      flushEn()
    }
    getMapsFromTsv(enPrepared)
  }

  private def flushRu() {
    val fw = new FileWriter(ruPrepared)
    parseDump(rudump, "utf8") {
      (id, title) => fw.write("%s\t%s\n".format(id, title))
    }
    log.info("Flushing done.")
    fw.close()
  }

  private def flushEn() {
    val fw = new FileWriter(enPrepared)
    parseDump(endump, "utf8") {
      (id, title) => fw.write("%s\t%s\n".format(id, title))
    }
    log.info("Flushing done.")
    fw.close()
  }
}