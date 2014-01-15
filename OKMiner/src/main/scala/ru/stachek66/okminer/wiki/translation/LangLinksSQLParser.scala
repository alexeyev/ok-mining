package ru.stachek66.okminer.wiki.translation

import java.io.{FileReader, BufferedReader, File}
import org.slf4j.LoggerFactory
import scala.util.{Success, Failure, Try}

/**
 * @author alexeyev
 */
object LangLinksSQLParser {

  private val log = LoggerFactory.getLogger("")

  private val rudump = new File("../ruwiki-latest-langlinks.sql")
  private val endump = new File("../enwiki-latest-langlinks.sql")
  private val extdump = new File("../extwiki-20131231-langlinks.sql")
  private val pattern = "\\((\\d+,'%s','([^']+)')\\)"

  private val rup = pattern.format("ru").r
  private val enp = pattern.format("en").r

  private val map = collection.mutable.Map[Long, String]()

  def parseFile(dump: File, enc: String) {

    // due to malformed input, we have to use buffered reader  :(
    val br = new BufferedReader(new FileReader(dump))

    while (br.ready()) Try {
      val line = br.readLine()
      if (line.contains("INSERT")) {
        enp.findAllIn(line).matchData.foreach(
          m => {
            val spl = m.group(1).split(",")
            val id = spl(0).toLong
            val tal = m.group(2).trim.replace("_", " ")
            map.put(id, tal)
          })
      }
    } match {
      case Failure(e) => println(e.getStackTraceString)
      case Success(s) => log.debug("wow such line")
    }
  }

  lazy val idToEnTitle = {
    parseFile(rudump, "iso-8859-1")
    map
  }
}