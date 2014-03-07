import java.io.FileWriter
import java.util.{Calendar, Date}
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.slf4j.LoggerFactory
import scala.annotation.tailrec
import scala.util._

/**
 * A tool for getting all links from Lenta.RU
 * for the specified category.
 * @author alexeyev
 */
object LentaLinksGetterTool extends App {

  private val log = LoggerFactory.getLogger(getClass)

  def previousDay(day: Date): Date = {
    val cal = Calendar.getInstance()
    cal.setTime(day)
    cal.add(Calendar.DAY_OF_YEAR, -1)
    cal.getTime()
  }

  def setDay(year: Int, month: Int, day: Int): Date = {
    val cal = Calendar.getInstance()
    cal.setTime(new Date())
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, month - 1)
    cal.set(Calendar.DAY_OF_MONTH, day)
    cal.getTime()
  }

  def ymd(day: Date) = {
    val cal = Calendar.getInstance()
    cal.setTime(day)
    (cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
  }

  def to2digits(number: Int): String = {
    if (number > 0 && number < 10)
      "0" + number
    else
      number.toString
  }

  @tailrec
  def getDocument(url: String): Document = {
    Try {
      Jsoup.connect(url).userAgent(math.random.toString).get
    } match {
      case Success(d) => d
      case Failure(e) => {
        log.error("Oops, problems while trying to connect", e)
        log.error(date + " " + ymd(date))
        log.error(url)
        Thread.sleep(1500)
        log.error("Retrying...")
        getDocument(url)
      }
    }
  }

  var date = setDay(2014, 3, 8)
  val category = "science"
  val urlPattern = "http://lenta.ru/rubrics/%s/%d/%s/%s"

  val fw = new FileWriter("../lenta-links-%s-%s.txt".format(category, new Date().getTime))

  while (ymd(date)._1 >= 1997) {
    val (year, month, day) = ymd(date)
    println(year, month, day)
    val url = urlPattern.format(category, year, to2digits(month), to2digits(day))

    val doc = getDocument(url)

    for (it <- doc.select("section.b-layout_archive").select("div.titles").toArray) {
      val slug = it.asInstanceOf[Element].select("a").attr("href")
      fw.write("http://lenta.ru%s\t%s/%s-%s\n".format(slug, year, month, day))
    }
    Thread.sleep(math.round(math.random * 1000 + 100))
    date = previousDay(date)
  }
  fw.close()
}
