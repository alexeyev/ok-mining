package ru.stachek66.okminer.categories

import org.slf4j.LoggerFactory
import ru.stachek66.okminer.utils.CounterLogger

/**
 * @author alexeyev
 */
object CategoryTreeTool extends App {

  case class Node(id: Long, t: String)

  val map = collection.mutable.Map[Int, (String, Array[Int])]()

  private val clog = new CounterLogger(
    LoggerFactory.getLogger("tax-tool"),
    500000,
    "%s lines processed")

  var counter = 0
  for {
    line <- io.Source.fromFile("wikiGraph.txt")("UTF-8").getLines()
  //    if counter < 5000000
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

  def pr(id: Int, sp: Int, maxDepth: Int): Unit = if (maxDepth >= sp) {
    for ((t, ch) <- map.get(id)) {
      println("|>" + (0 to sp).map(v => ".    ").mkString + t)
      for {
        c <- ch
        //hack due to cycles
        if c != id
      } pr(c, sp + 1, maxDepth)
    }
  }

  //tech: 384712

  pr(384712, sp = 2, maxDepth = 10)
}
