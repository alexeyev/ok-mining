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

  def parseFile(dump: File, enc: String): Map[Long, String] = {

    val idToTitleMap = collection.mutable.Map[Long, String]()

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
            idToTitleMap.put(id, tal)
          })
      }
    } match {
      case Failure(e) =>
        log.error("", e)
      case Success(s) => log.info("Line " + c)
    }
    idToTitleMap.toMap
  }

  //OOM danger (~5GiB)
  lazy val ruIdToTitle: Map[Long, String] = {
    log.info("Does ru-dump exist? " + rudump.exists())
    if (rudump.exists()) parseFile(rudump, "utf8")
    else Map()
  }

  //OOM danger (~5GiB)
  lazy val enIdToTitle: Map[Long, String] = {
    log.info("Does ru-dump exist? " + endump.exists())
    if (endump.exists()) parseFile(endump, "utf8")
    else Map()
  }

  def flush() {
    val fw = new FileWriter("parsed/ru-id-title.tsv")
    for {
      (id, title) <- idToTitle
    } {
      fw.write("%s\t%s\n".format(id, title))
    }
    fw.close()
  }
}
