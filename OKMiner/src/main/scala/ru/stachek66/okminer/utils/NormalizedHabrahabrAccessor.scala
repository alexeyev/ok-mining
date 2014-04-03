package ru.stachek66.okminer.utils

import java.io.{FileWriter, InputStream, FileInputStream, File}
import ru.stachek66.okminer.language.russian.Lemmatizer
import annotation.tailrec
import org.slf4j.LoggerFactory

/**
 * Provides access to lemmatized Habrahabr companies' names.
 * @author alexeyev
 */
@deprecated // since April due to deprecation of Zaliznyak lemmatizer
object NormalizedHabrahabrAccessor {

  private val parsedHabrahabr = new File("parsed/habr-normalized.tsv")
  private val clog = new CounterLogger(
    LoggerFactory.getLogger("habr-normalizer"),
    50,
    "%s companies normalized"
  )

  /**
   * Normalized (lemmatized) companies' names from habrahabr.ru.
   */
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
