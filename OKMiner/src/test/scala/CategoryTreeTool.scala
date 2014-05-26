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
        idToTextMap.put(-id, text)//"*" + text)
      } else if (unknown == 0) {
        val (parents, restt) = getByHead(rest)
        val (children, _) = getByHead(restt)
        idMap.put(id, children)
        idToTextMap.put(id, text)
      }
    }
  }

  def pr(id: Int, sp: Int, maxDepth: Int, act: (Int, Option[Int]) => Unit, parent: Option[Int]): Unit = if (maxDepth >= sp) {
    val children = idMap.get(id)
    if (id == -356842 || id == -2092320 ||  id == -236874) {
      act(id, parent)
    }
    for (c <- children) {
      pr(c, sp + 1, maxDepth, act, Some(id))
    }
  }

  println("Traversing tree")

  val a = new Date()
  //shape=box,fillcolor="palegreen",style="filled,rounded"
  println("digraph etane { \nnode [shape=box, style=\"rounded\"];")

  val visited = new collection.mutable.HashSet[String]()

  pr(384712, 0, 12, {
    case (id, Some(parent)) =>
      if (!visited.contains(id + " " + parent)) {
        visited.add(id + " " + parent)
        val son = idToTextMap(id)
        if (son.startsWith("*")) {
          if (!visited.contains(son)) {
            println("\"%s\" [shape=box,fillcolor=\"gray\",style=\"filled,rounded\"]; ".format(son.replace("*", "")))
            visited.add(son)
          }
          println("\"%s\" -> \"%s\";".format(idToTextMap(parent), son.replace("*", "")))
        } else {
          println("\"%s\" -> \"%s\";".format(idToTextMap(parent), son))
        }
      }
    case _ =>
  }, None)
  println("}")
  val b = new Date()

  println("it took " + TimeUnit.SECONDS.convert(b.getTime - a.getTime, TimeUnit.MILLISECONDS))

}


/*

digraph etane {
node [shape=box, style="rounded"];
"awk" [shape=box,fillcolor="gray",style="filled,rounded"];
"refal" [shape=box,fillcolor="gray",style="filled,rounded"];
"tree \nprogramming \nlanguages" -> "refal";
"functional\n languages" -> "refal";
"lisp (programming language)" [shape=box,fillcolor="gray",style="filled,rounded"];
"functional\n languages" -> "lisp (programming language)";
"extensible \nsyntax \nprogramming \nlanguages" -> "lisp (programming language)";
"pattern matching \nprogramming languages" -> "awk";
"pattern matching \nprogramming languages" -> "refal";
"text-oriented \nprogramming \nlanguages" -> "awk";
"term-rewriting \nprogramming \nlanguages" -> "refal";
"dynamically typed\n programming \nlanguages" -> "lisp (programming language)";
"scripting languages" -> "awk";
}


 */
