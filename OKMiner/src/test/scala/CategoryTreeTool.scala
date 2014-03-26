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
    100000,
    "%s lines processed")

  var counter = 0
  for {
    line <- io.Source.fromFile("wikiGraph.txt").getLines()
  //    if counter < 5000000
  } clog.execute {
    counter += 1
    if (counter > 1) {
      val data = line.split("\t").toList

      val id = data(0).toInt
      //    val wi = data(1).toInt
      val text = data(2)
      val unknown = data(3).toInt
      val (head, rest) = data.splitAt(4)

      def getByHead(a: List[String]): (List[Int], List[String]) = {
        val num = a.head.toInt
        (a.tail.take(num).map(_.toInt), a.splitAt(num + 1)._2)
      }

      if (unknown == 1) {
        val (parents, restt) = getByHead(rest)
//        val (children, _) = getByHead(restt)
        map.put(-id, (text, parents.map(-_).toArray))
      } else if (unknown == 0) {
        val (parents, restt) = getByHead(rest)
        val (children, _) = getByHead(restt)
        map.put(id, (text, children.toArray))
      }
    }
  }

  def pr(id: Int, sp: Int, maxDepth: Int): Unit = if (maxDepth >= sp) {
    for ((t, ch) <- map.get(id)) {
      println("|>" + (0 to sp).map(v => "*").mkString + t)
      for (c <- ch) pr(c, sp + 1, maxDepth)
    }
  }

  pr(-3, 0, 2)
}
