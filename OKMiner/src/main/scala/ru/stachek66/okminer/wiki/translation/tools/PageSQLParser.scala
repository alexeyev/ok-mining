package ru.stachek66.okminer.wiki.translation.tools

import java.io._
import java.util.zip.GZIPInputStream
import org.slf4j.LoggerFactory
import scala.util.Failure
import scala.util.Success
import scala.util.{Success, Failure, Try}
import ru.stachek66.okminer.utils.CounterLogger

/**
 * Parses id-to-title mapping for Russian wiki-articles
 * @author alexeyev
 */
private[tools] object PageSQLParser {

  private val log = LoggerFactory.getLogger("")
  private val clog = new CounterLogger(log, 50, "%s wiki lines processed")

  private[translation] val rudump = new File("../meta_data/ruwiki-latest-page.sql.gz")
  private[translation] val endump = new File("../meta_data/enwiki-latest-page.sql.gz")

  new File("parsed").mkdirs()

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
      case Failure(e) => log.error("Problem", e)
      case Success(s) => clog.execute(())
    }
  }

  def flushRu() {
    val fw = new FileWriter(ruPrepared)
    parseDump(rudump, "utf8") {
      (id, title) => fw.write("%s\t%s\n".format(id, title))
    }
    log.info("Flushing done.")
    fw.close()
  }

  def flushEn() {
    val fw = new FileWriter(enPrepared)
    parseDump(endump, "utf8") {
      (id, title) => fw.write("%s\t%s\n".format(id, title))
    }
    log.info("Flushing done.")
    fw.close()
  }
}