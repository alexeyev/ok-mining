import java.io.FileWriter
import java.util.{Calendar, Date}
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import scala.util._

/**
 * A tool for getting all articles from Lenta.RU
 * for the specified category.
 * @author alexeyev
 */
object LentaGetterTool extends App {

  def previousDay(day: Date): Date = {
    val cal = Calendar.getInstance()
    cal.setTime(day)
    cal.add(Calendar.DAY_OF_YEAR, -1)
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

  var date = new Date()
  val category = "science"
  val urlPattern = "http://lenta.ru/rubrics/%s/%d/%s/%s"

  val fw = new FileWriter("../lenta-links-%s.txt".format(category))

  while (true) {
    date = previousDay(date)
    val (year, month, day) = ymd(date)
    println(year, month, day)
    val url = urlPattern.format(category, year, to2digits(month), to2digits(day))
    val doc = Try {
      Jsoup.connect(url).userAgent("Mozilla").get
    } match {
      case Success(d) => d
      case Failure(e) => {
        println(e.getStackTrace)
        println(date, ymd(date))
        println(url)
        fw.close()
        throw e
      }
    }

    for (it <- doc.select("section.b-layout_archive").select("div.titles").toArray) {
      val slug = it.asInstanceOf[Element].select("a").attr("href")
      fw.write("http://lenta.ru%s\n".format(slug))
    }
    Thread.sleep(math.round(math.random * 1000 + 100))
  }
  fw.close()
}
