import java.util.concurrent.TimeUnit
import java.util.Date
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.utils.{MutableChainMap, CounterLogger}

/**
 * @author alexeyev
 */
object CategoryTreeTool extends App {

  case class Node(id: Long, t: String)

  val idMap = new MutableChainMap[Int, Int]()
  val idToTextMap = collection.mutable.Map.empty[Int, String]

  private val clog = new CounterLogger(
    LoggerFactory.getLogger("tax-tool"),
    100000,
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
        for (parent <- parents) idMap.put(parent, -id)
        idToTextMap.put(-id, "*" + text)
      } else if (unknown == 0) {
        val (parents, restt) = getByHead(rest)
        val (children, _) = getByHead(restt)
        idMap.put(id, children)
        idToTextMap.put(id, text)
      }
    }
  }

  def pr(id: Int, sp: Int, maxDepth: Int, act: String => Unit): Unit = if (maxDepth >= sp) {
    for {
      title <- idToTextMap.get(id)
      children = idMap.get(id)
    } {
      act(title)
      for (c <- children) pr(c, sp + 1, maxDepth, act)
    }
  }

  val a = new Date()
  pr(384712, 0, 7, title => println("wow such " + title))
  val b = new Date()

  println("it took " + TimeUnit.SECONDS.convert(b.getTime - a.getTime, TimeUnit.MILLISECONDS))

}
