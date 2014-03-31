package ru.stachek66.okminer

import java.io.File
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.Meta.singleContext
import ru.stachek66.okminer.utils.{StatsFileIO, CounterLogger}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
 * A tool for processing the whole corpus:
 * it reads from files and writes to files provided
 * with necessary directories.
 * @author alexeyev
 */
object CorpusProcessorWriter {

  private val processor = new OneYearProcessor()
  private val yearPattern = "\\d{1,4}".r.pattern
  private val clog = new CounterLogger(LoggerFactory.getLogger("corpus-global-processed"), 100, "%s files processed")

  /**
   * Writing the results of the whole corpus processing to the specified folder.
   * @param corpus corpus directory; it should contain folders with names matching "\\d{1,4}" with texts in them
   * @param reportsDirectory directory for writing tsv reports into
   */
  def processCorpus(corpus: File, reportsDirectory: File) {

    reportsDirectory.mkdirs()

    val tasks = for {
    // choosing appropriate directories
      directory <- corpus.listFiles().toIterable
      if directory.isDirectory &&
        yearPattern.matcher(directory.getName).matches() &&
        directory.listFiles().nonEmpty
    } yield {
      // running in parallel
      scala.concurrent.future[Unit] {
        val log = new CounterLogger(LoggerFactory.getLogger(directory.getName + "-processor"), 10, "%s files processed")
        log.getLogger.info("I'm parsing " + directory.getName)
        // carrying out the core task
        val data = processor.extractFromYearDirectory(directory, List(log, clog))
        // writing everything down
        StatsFileIO.writeToFile(data, new File(s"${reportsDirectory.getAbsolutePath}/${directory.getName}.tsv"))
        log.getLogger.info("Done with it!")
      }
    }
    // waiting for 48 hours max
    Await.ready(Future.sequence(tasks), Duration(2, TimeUnit.DAYS))
  }
}