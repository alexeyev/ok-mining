package ru.stachek66.okminer.wiki.translation

import java.io.{FileWriter, FileReader, BufferedReader, File}
import org.slf4j.LoggerFactory
import scala.util.{Success, Failure, Try}

/**
 * Parses id-to-title mapping for Russian wiki-articles
 * @author alexeyev
 */
object PageSQLParser {

  private val log = LoggerFactory.getLogger("")

  private val rudump = new File("../ruwiki-latest-page.sql")
  private val endump = new File("../enwiki-latest-page.sql")
  private val pattern = "\\((\\d+),\\d+,'([^']+)'".r

  def parseFile(dump: File, enc: String): (Map[Long, String], Map[String, Long]) = {

    val idToTitleMap = collection.mutable.Map[Long, String]()
    val titleToIdMap = collection.mutable.Map[String, Long]()

    // due to malformed input, we have to use buffered reader  :(
    val br = new BufferedReader(new FileReader(dump))
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
            titleToIdMap.put(tal, id)
            idToTitleMap.put(id, tal)
          })
      }
    } match {
      case Failure(e) => log.error("", e)
      case Success(s) => log.info("Line " + c)
    }
    log.info("Converting to maps... " + idToTitleMap.size + " " + titleToIdMap.size)
    (idToTitleMap.toMap, titleToIdMap.toMap)
  }

  lazy val (ruIdToTitle, ruTitleToId): (Map[Long, String], Map[String, Long]) = {
    log.info("Does ru-dump exist? " + rudump.exists())
    if (rudump.exists()) parseFile(rudump, "utf8")
    else (Map(), Map())
  }

  lazy val (enIdToTitle, enTitleToId): (Map[Long, String], Map[String, Long]) = {
    log.info("Does en-dump exist? " + endump.exists())
    if (endump.exists()) parseFile(endump, "utf8")
    else (Map(), Map())
  }

  def flush() {
    val fw = new FileWriter("parsed/ru-id-title.tsv")
    for {
      (title, id) <- ruIdToTitle
    } {
      fw.write("%s\t%s\n".format(id, title))
    }
    fw.close()
  }
}
