import java.io.FileWriter
import java.net.URL
import java.nio.file.{Paths, Files}
import java.util.Date
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.utils.CounterLogger
import scala.annotation.tailrec
import scala.util._

/**
 * @author alexeyev
 */
object LentaDownloader extends App {

  private val log = LoggerFactory.getLogger(getClass)
  private val clog = new CounterLogger(log, 10, "%s files downloaded")

  @tailrec
  def getPage(url: URL, rawDate: String) {
    Try {
      Files.copy(url.openStream(), Paths.get(s"corpus/raw/$rawDate--" + clog.getCurrentCount))
    } match {
      case Success(_) => clog.execute(())
      case Failure(e) => {
        log.error(url.toString, e)
        Thread.sleep(10000)
        getPage(url, rawDate)
      }
    }
  }

  for {
    line <- io.Source.fromFile("/home/alexeyev/thesis/lenta-links-science-all3.txt")("UTF-8").getLines()
    splitted = line.split("\t")
    rawUrl = splitted(0)
    rawDate = splitted(1).replace("/", ".")
    url = new URL(rawUrl)
  } getPage(url, rawDate)
}
