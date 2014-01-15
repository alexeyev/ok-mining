package ru.stachek66.okminer.wiki.translation

import java.io.{FileWriter, FileReader, BufferedReader, File}
import org.slf4j.LoggerFactory
import scala.util.{Success, Failure, Try}

/**
 * @author alexeyev
 */
object PageSQLParser {

  private val log = LoggerFactory.getLogger("")

  private val rudump = new File("../ruwiki-latest-page.sql")
  private val pattern = "\\((\\d+),\\d+,'([^']+)'".r


  private val map = collection.mutable.Map[Long, String]()

  def parseFile(dump: File, enc: String) {

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
            map.put(id, tal)
          })
      }
    } match {
      case Failure(e) =>
        println(e.getStackTraceString)
        log.error("", e)
      case Success(s) => log.info("wow such line " + c)
    }
  }

  //OOM danger (~5GiB)
  lazy val idToTitle = {
    println(new File("../").listFiles())
    println(rudump)
    println(rudump.exists())
    parseFile(rudump, "utf8")
    map
  }

  def flush() {
    val fw = new FileWriter("ru-id-title.tsv")
    for {
      (id, title) <- idToTitle
    } {
      fw.write("%s\t%s\n".format(id, title))
    }
    fw.close()
  }
}
