package ru.stachek66.okminer.wiki.translation.tools

import java.io._
import java.util.zip.GZIPInputStream
import org.slf4j.LoggerFactory
import scala.util.Failure
import scala.util.Success
import scala.util.matching.Regex
import scala.util.{Success, Failure, Try}
import ru.stachek66.okminer.utils.CounterLogger

/**
 * Parsing langlinks dump.
 * @author alexeyev
 */
private[tools] object LangLinksSQLParser {

  private val log = LoggerFactory.getLogger("langlinks-parser")
  private val clog = new CounterLogger(log, 50, "%s lines processed")

  private val rudump = ru.stachek66.okminer.wiki.dumps("ru_langlinks")
  private val endump = ru.stachek66.okminer.wiki.dumps("en_langlinks")

  private val pattern = "\\((\\d+,'%s','([^']+)')\\)"

  private val rup = pattern.format("ru").r
  private val enp = pattern.format("en").r

  private def parseFile(dump: File, enc: String, langRegex: Regex): Map[Long, String] = {

    val map = collection.mutable.Map[Long, String]()

    // due to malformed input, we have to use buffered reader  :(
    val br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(dump))))
    var counter = 0

    while (br.ready()) Try {
      counter += 1
      val line = br.readLine()
      if (line.contains("INSERT")) {
        langRegex.findAllIn(line).matchData.foreach(
          m => {
            val spl = m.group(1).split(",")
            val id = spl(0).toLong
            val tal = m.group(2).trim.replace("_", " ")
            map.put(id, tal)
          })
      }
    } match {
      case Failure(e) => log.error("Problem", e)
      case Success(s) => clog.execute(())
    }
    map.toMap
  }

  lazy val idToEnTitle = {
    parseFile(rudump, "iso-8859-1", enp)
  }

  lazy val idToRuTitle = {
    parseFile(endump, "iso-8859-1", rup)
  }
}