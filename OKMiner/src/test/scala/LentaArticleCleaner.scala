import java.io.{FileWriter, File}
import java.text.SimpleDateFormat
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
  private val errlog = new CounterLogger(log, 2, "%s fuckups")

  def parseText(html: String): String = {
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

  private val dayPattern = ".* (\\d+) ([А-Яа-я]+) (\\d{4}).*".r

  def parseDate(html: String): Date = {
    //http://lenta.ru/news/2014/02/05/lm100j/
    val doc = Jsoup.parse(html)


    val rawDate = doc.select(".b-topic__info").select(".g-date").text()
    println(rawDate)
    rawDate match {
      case dayPattern(day, monthRaw, year) =>
        println(day, monthRaw, year)
        val month =
          if (monthRaw.startsWith("дек")) 12
          else if (monthRaw.startsWith("янв")) 1
          else if (monthRaw.startsWith("фев")) 2
          else if (monthRaw.startsWith("мар")) 3
          else if (monthRaw.startsWith("апр")) 4
          else if (monthRaw.startsWith("май")) 5
          else if (monthRaw.startsWith("июн")) 6
          else if (monthRaw.startsWith("июл")) 7
          else if (monthRaw.startsWith("авг")) 8
          else if (monthRaw.startsWith("сен")) 9
          else if (monthRaw.startsWith("окт")) 10
          else 11
        LentaLinksGetterTool.setDay(year.toInt, month, day.toInt)
      case _ => {
        val sdate = doc.select("div.b-topic__info").select("time.g-date").attr("datetime")
        if (!sdate.trim.equals("")) {
          println(sdate)
          new SimpleDateFormat("yyyy-MM-dd").parse(sdate)
        } else {
          log.info("fffuu " + doc.title())
          null
        }
      }
    }
  }

  def dateToFolder(date: Date): String = {
    val (year, month, day) = LentaLinksGetterTool.ymd(date)
    "%s/%s/%s".format(year, month, day)
  }

  for (file <- new File("corpus/raw/").listFiles()) {
    clog.tick()
    val html = FileUtils.asStringWithoutNewLines(file)
    Try {
      val path = new File("corpus/clean/" + dateToFolder(parseDate(html)))
      path.mkdirs()
      val fw = new FileWriter(path.getAbsolutePath + new Date().getTime)
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

