package ru.stachek66.okminer.wiki.translation

import java.io.{FileReader, BufferedReader, File}
import org.slf4j.LoggerFactory
import scala.util.{Success, Failure, Try}
import scala.util.matching.Regex

/**
 * PArses langlinks dump.
 * @author alexeyev
 */
object LangLinksSQLParser {

  private val log = LoggerFactory.getLogger("")

  private val rudump = new File("../ruwiki-latest-langlinks.sql")
  private val endump = new File("../enwiki-latest-langlinks.sql")
  //  private val extdump = new File("../extwiki-20131231-langlinks.sql")
  private val pattern = "\\((\\d+,'%s','([^']+)')\\)"

  private val rup = pattern.format("ru").r
  private val enp = pattern.format("en").r

  def parseFile(dump: File, enc: String, langRegex: Regex): Map[Long, String] = {

    val map = collection.mutable.Map[Long, String]()

    // due to malformed input, we have to use buffered reader  :(
    val br = new BufferedReader(new FileReader(dump))
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
      case Failure(e) => println(e.getStackTraceString)
      case Success(s) => log.info("Wow such line " + counter)
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