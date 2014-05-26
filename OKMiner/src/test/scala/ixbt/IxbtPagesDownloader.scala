package ixbt

import scala.annotation.tailrec
import java.net.URL
import java.nio.file.{Files, Paths}
import scala.util.{Success, Failure, Try}
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.utils.CounterLogger
import java.security.MessageDigest

/**
 * Created by alexeyev on 26.05.14.
 */
object IxbtPagesDownloader extends App {

  val log = LoggerFactory.getLogger(getClass)
  val clog = new CounterLogger(log, 30, "%s pages downloaded")

  def md5(s: String) = MessageDigest.getInstance("MD5").digest(s.getBytes).mkString

  @tailrec
  def getPage(url: URL, year: Int) {
    val urlHash = md5(url.getPath)
    val path = Paths.get(s"../ixbt/raw/pages/$year/${clog.getCurrentCount}$urlHash")
    Try {
      Files.copy(
        url.openStream(),
        path)
    } match {
      case Failure(e: Exception) =>
        log.error(url.toString)
        Thread.sleep(10000)
        path.getParent.toFile.mkdirs()
        getPage(url, year)
      case Success(_) => clog.execute(())
    }
  }

  for (line <- io.Source.fromFile("../ixbt/raw/hard_links.tsv").getLines()) {

    val year :: url :: _ = line.split("\t").toList
    getPage(new URL(url), year.toInt)
    Thread.sleep(math.random(5000) + 500)

  }
}
