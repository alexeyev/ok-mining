package ru.stachek66.okminer.wiki.translation

import java.io.{FileReader, BufferedReader, File}
import scala.collection.mutable.ArrayBuffer
import scala.util.{Success, Failure, Try}
import org.slf4j.LoggerFactory

/**
 * @author alexeyev
 */
object LangLinksSQLParser {

  private val log = LoggerFactory.getLogger("")

  private val rudump = new File("../ruwiki-latest-langlinks.sql")
  private val endump = new File("../enwiki-latest-langlinks.sql")
  private val extdump = new File("/home/alexeyev/thesis/extwiki-20131231-langlinks.sql")
  private val pattern = "\\((\\d+,'%s','[^']+')\\)"

  private val rup = pattern.format("ru").r
  private val enp = pattern.format("en").r

  private val map = collection.mutable.Map[Long, String]()

  def parseFile(dump: File, enc: String) {

    // due to malformed input, we have to use buffered reader  :(
    val br = new BufferedReader(new FileReader(dump))

    while (br.ready()) Try {
      val line = br.readLine()
      if (line.contains("INSERT")) {
        //        rup.findAllIn(line).matchData.foreach(
        //          m => {
        //            val spl = m.group(1).split(",")
        //            val id = spl(0).toLong
        //            val tal = spl.tail.tail.mkString(",")
        //            map.get(id) match {
        //              case Some(a) => map.put(id, a ++ ArrayBuffer(tal))
        //              case None => map.put(id, ArrayBuffer(tal))
        //            }
        //          })
        enp.findAllIn(line).matchData.foreach(
          m => {
            val spl = m.group(1).split(",")
            val id = spl(0).toLong
            val tal = spl.tail.tail.mkString(",")
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
  //
  //    parseFile(rudump, "iso-8859-1")
  //  //  parseFile(endump, "utf8")
  //  //  parseFile(extdump, "utf8")
  //  println(map.size)
  //  //  map.foreach(println(_))
}