package ru.stachek66.okminer.categories

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.utils.{MutableChainMap, CounterLogger}
import java.io._
import java.util.zip.GZIPInputStream

/**
 * Extracting and holding category taxonomy from wikipedia dump.
 * Data are large, which is why this is a singleton object.
 * @author alexeyev
 */
object TechCategories {

  private val technologyId = 384712
  private val log = LoggerFactory.getLogger("taxonomy-tool")
  private val clog = new CounterLogger(log, 500000, "%s lines processed")

  private def parseDump() {

    // a bit of redundancy
    val chMap = new MutableChainMap[Int, Int]()
    val psMap = new MutableChainMap[Int, Int]()
    val idToText = collection.mutable.Map[Int, String]()

    var counter = 0

    // filling the tree
    for {
      line <- io.Source.fromInputStream(new GZIPInputStream(dump))("UTF-8").getLines()
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

        // articles
        if (unknown == 1) {
          val (parents, restt) = getByHead(rest)
          //        val (children, _) = getByHead(restt)
          for (parent <- parents) {
            chMap.put(parent, -id)
            psMap.put(-id, parent)
          }
          idToText.put(-id, text)
        } else
        // category tree
        if (unknown == 0) {
          //children and parents are extracted from category tree
          val (parents, restt) = getByHead(rest)
          val (children, _) = getByHead(restt)
          chMap.put(id, children)
          psMap.put(id, parents)
          idToText.put(id, text)
        }
      }
    }

    /**
     * Traversing categories.
     * @param id current article id
     * @param sp tree depth (number of spaces for pretty-printing)
     * @param maxDepth maximum depth of a search tree
     * @param onEach action to be applied to each visited node
     */
    def pr(id: Int, sp: Int, maxDepth: Int, onEach: Int => Unit): Unit =
      if (maxDepth >= sp) {
        val children = chMap.get(id)
        onEach(id)
        log.debug("|>" + (0 to sp).map(v => ". ").mkString + id)
        for {
          c <- children
          //hack due to loops
          if c != id
        } pr(c, sp + 1, maxDepth, onEach)
      }

    /*
    So we start from category Technology and go down no more than K levels,
    filling child-to-parent map.
     */
    pr(technologyId, sp = 0, maxDepth = 8, {
      id => println(psMap.get(id))
    })

    /*
     * Flushing child-to-parent map to file.
     */
    //    val fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(parsed), "UTF-8"))
    //    for {(element, parent) <- parentMap} {
    //      fw.write(s"$element\t${parent.getOrElse("_")}\n")
    //    }
    //    System.gc()
    //    fw.close()
  }

  /**
   * Getting child-to-parent map from preprocessed file.
   */
  private def fromParsed: Map[String, String] =
    io.Source.fromFile(parsed)("UTF-8").getLines().
      map(
      line => clog.execute {
        val splitted = line.trim.split("\t")
        splitted(0) -> splitted(1)
      }
    ).toMap

  /**
   * All English categories with acceptable BFS-distance from
   * "Technology" mapped on their parent nodes
   */
  lazy val acceptableTopics: Map[String, String] = {
    if (!parsed.exists() || parsed.length() <= 0) {
      log.info("Will have to parse wiki-graph dump...")
      parseDump()
    }
    log.info("Getting acceptable topics...")
    val set = fromParsed
    log.info("Topics got.")
    set
  }

  override def main(args: Array[String]) {
    parseDump()
  }
}

