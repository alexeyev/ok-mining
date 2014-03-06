import java.io.{FileWriter, File}
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.utils.{CounterLogger, FileUtils}

/**
 * @author alexeyev
 */
object LentaArticleCleaner extends App {

  private val log = LoggerFactory.getLogger(getClass)
  private val clog = new CounterLogger(log, 100, "%s files cleaned")

  def parse(html: String): String = {
    val doc = Jsoup.parse(html)
    val title = doc.select(".b-topic__title").text().trim
    val mainBody =
      doc.select("div.b-topic__content").
        select("p").
        toArray().
        map(_.asInstanceOf[Element].text()).
        mkString("\n")
    title + " " + mainBody
  }

  for (file <- new File("corpus/raw/").listFiles()) {
    clog.tick()
    val html = FileUtils.asStringWithoutNewLines(file)
    val fw = new FileWriter("corpus/clean/" + file.getName)
    fw.write(parse(html))
    fw.close()
  }
}
