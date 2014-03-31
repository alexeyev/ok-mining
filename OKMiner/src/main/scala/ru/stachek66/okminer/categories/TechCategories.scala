package ru.stachek66.okminer.categories

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.utils.CounterLogger
import java.io.FileWriter

/**
 * Extracting category taxonomy from wikipedia dump.
 * @author alexeyev
 */
object TechCategories {

  private val technologyId = 384712

  private val log = LoggerFactory.getLogger("taxonomy-tool")
  private val clog = new CounterLogger(log, 500000, "%s lines processed")

  private def parseDump() {

    var map = collection.mutable.Map[Int, (String, Array[Int])]()
    val set = collection.mutable.Set[String]()

    var counter = 0
    for {
      line <- io.Source.fromFile(dump)("UTF-8").getLines()
    } clog.execute {
      counter += 1
      if (counter > 1) {
        val data = line.split("\t").toList

        val id = data(0).toInt
        val text = data(2)
        val unknown = data(3).toInt
        val (_, rest) = data.splitAt(4)

        def getByHead(a: List[String]): (List[Int], List[String]) = {
          val num = a.head.toInt
          (a.tail.take(num).map(_.toInt), a.splitAt(num + 1)._2)
        }

        if (unknown == 1) {
          //        val (parents, restt) = getByHead(rest)
          //        val (children, _) = getByHead(restt)
          //        map.put(-id, (text, children.map(-_).toArray))
        } else
        if (unknown == 0) {
          //children and parents are extracted OK
          val (parents, restt) = getByHead(rest)
          val (children, _) = getByHead(restt)
          map.put(id, (text, children.toArray))
        }
      }
    }

    def pr(id: Int, sp: Int, maxDepth: Int, onEach: String => Unit): Unit =
      if (maxDepth >= sp) {
        for ((t, ch) <- map.get(id)) {
          onEach(t)
          //log.debug("|>" + (0 to sp).map(v => ". ").mkString + t)
          for {
            c <- ch
            //hack due to cycles
            if c != id
          } pr(c, sp + 1, maxDepth, onEach)
        }
      }

    pr(technologyId, sp = 0, maxDepth = 5, topic => set.add(topic))
    map = null

    val fw = new FileWriter(parsed)
    for (element <- set) {
      fw.write(element + "\n")
    }
    fw.close()
  }

  private def fromParsed: Set[String] =
    io.Source.fromFile(parsed)("UTF-8").getLines().map(_.trim).toSet

  lazy val acceptableTopics: Set[String] = {
    if (!parsed.exists() || parsed.length() <= 0) {
      log.info("Will have to parse wiki-graph dump...")
      parseDump()
    }
    log.info("Getting acceptable topics...")
    val set = fromParsed
    log.info("Topics got.")
    set
  }
}