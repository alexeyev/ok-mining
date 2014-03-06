import java.io.FileWriter
import java.net.URL
import org.apache.commons.io.IOUtils

/**
 * @author alexeyev
 */
object LentaDownloader extends App {

//  val pattern = ".*/([^/]+)/?$".r
  var counter = 0
  for {
    line <- io.Source.fromFile("../lenta-links-science-all").getLines()
    splitted = line.split("\t")
    rawUrl = splitted(0)
    rawDate = splitted(1).replace("/", ".")
    url = new URL(rawUrl)
  } {
    counter += 1
    val html = IOUtils.toString(url.openStream(), "UTF-8")
    val fw = new FileWriter(s"corpus/raw/$rawDate--" + counter)
    fw.write(html)
    fw.close()
  }
}
