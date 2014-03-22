import java.io.{FileWriter, File}
import java.util.Date
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.utils.{CounterLogger, FileUtils}
import scala.util._

/**
 * @author alexeyev
 */
object LentaArticleCleaner extends App {

  private val log = LoggerFactory.getLogger(getClass)
  private val clog = new CounterLogger(log, 100, "%s files cleaned")
  private val errlog = new CounterLogger(log, 2, "%s fups")

  private val category = "media"

  def parseText(html: String): String = {
    val doc = Jsoup.parse(html)
    val title = doc.select(".b-topic__title").text().trim
    val mainBody =
      doc.select("div.b-topic__body").
        select("p").
        toArray().
        map(_.asInstanceOf[Element].text()).
        mkString("\n")
    title + " " + mainBody
  }

  def folderName(file: File): String = {
    file.getName.split("\\.")(0)
  }

  def fileName(file: File): String = {
    file.getName.split("\\.")(1) + new Date().getTime
  }

  for (file <- new File(s"corpus-$category/raw/").listFiles()) {
    clog.tick()
    val html = FileUtils.asStringWithoutNewLines(file)
    Try {
      val path = new File(s"corpus-$category/clean/" + folderName(file))
      path.mkdirs()
      val fw = new FileWriter(path.getAbsolutePath + "/" + fileName(file))
      fw.write(parseText(html))
      fw.close()
    } match {
      case Success(_) => log.debug("cool")
      case Failure(e) =>
        log.error(file.getName)
        errlog.tick()
    }
  }
}