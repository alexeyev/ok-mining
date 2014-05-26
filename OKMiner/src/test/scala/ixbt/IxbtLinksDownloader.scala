package ixbt

import annotation.tailrec
import java.io.File
import java.net.URL
import java.nio.file.{Paths, Files}
import java.util.concurrent.TimeUnit
import java.util.{Calendar, Date}
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.utils.CounterLogger
import scala.util._

/**
 * @author alexeyev
 */
object IxbtLinksDownloader extends App {

  /*

  http://www.ixbt.com/news/hard/archive.shtml?2001/1114

   */

  new File("../ixbt/raw/").mkdirs()

  private val log = LoggerFactory.getLogger(getClass)
  private val clog = new CounterLogger(log, 10, "%s files downloaded")
  private val cal = Calendar.getInstance()

  def yearByDate(date: Date): Int = synchronized {
    cal.setTime(date)
    cal.get(Calendar.YEAR)
  }

  def urlByDate(date: Date): String = synchronized {
    cal.setTime(date)
    s"http://www.ixbt.com/news/soft/archive.shtml?${
      cal.get(Calendar.YEAR)
    }/${
      cal.get(Calendar.MONTH)
    }${
      cal.get(Calendar.DAY_OF_MONTH)
    }"
  }

  @tailrec
  def getPage(url: URL, rawDate: Date) {
    val path = Paths.get(s"../ixbt/raw/soft/${yearByDate(rawDate)}/${clog.getCurrentCount}${System.currentTimeMillis()}")
    Try {
      Files.copy(
        url.openStream(),
        path)
    } match {
      case Failure(e: Exception) =>
        log.error(url.toString, e)
        Thread.sleep(10000)
        path.getParent.toFile.mkdirs()
        getPage(url, rawDate)
      case Success(_) => clog.execute(())
    }
  }

  var currentDate = new Date()

  while (true) {
    val url = new URL(urlByDate(currentDate))
    getPage(url, currentDate)
    currentDate = new Date(currentDate.getTime - TimeUnit.DAYS.toMillis(1))
  }
}
