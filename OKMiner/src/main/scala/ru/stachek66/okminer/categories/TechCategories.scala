package ru.stachek66.okminer.categories

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.utils.{MutableChainMap, CounterLogger}
import java.util.zip.GZIPInputStream
import java.io.{FileOutputStream, OutputStreamWriter, BufferedWriter}

/**
 * Extracting and holding category taxonomy from wikipedia dump.
 * Data are large, which is why this is a singleton object.
 * @author alexeyev
 */
object TechCategories {

  private val technologyId = 384712
  private val log = LoggerFactory.getLogger("taxonomy-tool")
  private val clog = new CounterLogger(log, 100000, "%s lines processed")

  /**
   * Nota bene! Out-Of-Memory-Danger: > 2 GB required
   */
  private def parseDump() {

    // a bit of redundancy
    val chMap = new MutableChainMap[Int, Int]()
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
          }
          idToText.put(-id, "*" + text)
        } else
        // category tree
        if (unknown == 0) {
          //children and parents are extracted from category tree
          val (parents, restt) = getByHead(rest)
          val (children, _) = getByHead(restt)
          chMap.put(id, children)
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
    def pr(id: Int, sp: Int, maxDepth: Int, onEach: (Int, Option[Int]) => Unit, parent: Option[Int]): Unit =
      if (maxDepth >= sp) {
        onEach(id, parent)
        //log.debug("|>" + (0 to sp).map(v => ". ").mkString + id)
        for {
          c <- chMap.get(id)
          //hack due to loops
          if c != id
        } pr(c, sp + 1, maxDepth, onEach, Some(id))
      }

    log.info("Filling the map")

    val psMap = new MutableChainMap[Int, Int]()
    /*
    So we start from category Technology and go down no more than K levels,
    filling child-to-parent map.
     */
    pr(technologyId, sp = 0, maxDepth = 5, {
      case (id, pt) => psMap.put(id, pt)
    }, None)

    log.info("Writing to file")

    /*
     * Flushing child-to-parent map to file.
     */
    val fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(parsed), "UTF-8"))
    for {
      (id, parents) <- psMap.iterator
      element = idToText(id)
      textParents = parents.map(idToText(_)).toSet
    } {
      if (element.startsWith("*"))
        fw.write(s"${element.tail}\t${textParents.mkString("\t")}\n")
      else
        fw.write(s"$element\t${(textParents + element).mkString("\t")}\n")
    }
    System.gc()
    fw.close()
    log.info("Subtree flushed.")
  }

  /**
   * Getting child-to-parent map from preprocessed file.
   */
  private def fromParsed: Map[String, Array[String]] =
    io.Source.fromFile(parsed)("UTF-8").getLines().
      map(
      line => clog.execute {
        val splitted = line.trim.split("\t")
        splitted.head -> splitted.tail
      }
    ).toMap

  /**
   * All English categories with acceptable BFS-distance from
   * "Technology" mapped on their parent nodes
   */
  val acceptableTopics: Map[String, Array[String]] = {
    if (!parsed.exists() || parsed.length() <= 0) {
      log.info("Will have to parse wiki-graph dump...")
      parseDump()
    }
    log.info("Getting acceptable topics...")
    val set = fromParsed
    log.info("Topics got.")
    set
  } withDefaultValue Array.empty[String]
}

