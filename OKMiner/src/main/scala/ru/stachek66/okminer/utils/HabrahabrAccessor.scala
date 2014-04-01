package ru.stachek66.okminer.utils

import java.io.{FileWriter, InputStream, FileInputStream, File}
import ru.stachek66.okminer.language.russian.Lemmatizer
import annotation.tailrec
import org.slf4j.LoggerFactory

/**
 * @author alexeyev
 */
object HabrahabrAccessor {

  private val parsedHabrahabr = new File("parsed/habr-normalized.tsv")
  private val clog = new CounterLogger(
    LoggerFactory.getLogger("habr-normalizer"),
    50,
    "%s companies normalized"
  )

  @tailrec
  def getNormalizedStream: InputStream = {
    if (parsedHabrahabr.exists() && parsedHabrahabr.length() > 0) {
      new FileInputStream(parsedHabrahabr)
    } else {
      val fw = new FileWriter(parsedHabrahabr)
      for {
        line <- io.Source.fromInputStream(classOf[ClassLoader].getResourceAsStream("/habrahabr-companies.tsv")).getLines()
      } clog.execute {
        fw.write(Lemmatizer.lemmatize(line.trim).mkString(" ") + "\n")
      }
      fw.close()
      getNormalizedStream
    }
  }
}
