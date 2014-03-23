package ru.stachek66.okminer

import java.io.File
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.utils.{StatsFileIO, CounterLogger}

/**
 * A tool for processing the whole corpus:
 * it reads from files and writes to files provided
 * with necessary directories.
 * @author alexeyev
 */
object CorpusProcessorWriter {

  private val processor = new OneYearProcessor()
  private val yearPattern = "\\d{1,4}".r.pattern

  /**
   * Writing the results of the whole corpus processing to the specified folder.
   * @param corpus corpus directory; it should contain folders with names matching "\\d{1,4}" with texts in them
   * @param reportsDirectory directory for writing tsv reports into
   */
  def processCorpus(corpus: File, reportsDirectory: File) {

    for {
    // choosing appropriate directories
      directory <- corpus.listFiles().toIterable
      if directory.isDirectory &&
        yearPattern.matcher(directory.getName).matches() &&
        directory.listFiles().nonEmpty
    } {
      val log = new CounterLogger(LoggerFactory.getLogger(directory.getName + "-processor"), 1, "%s files processed")
      // carrying out the core task
      val data = processor.extractFromYearDirectory(directory, Some(log))
      reportsDirectory.mkdirs()
      // writing everything down
      StatsFileIO.writeToFile(data, reportsDirectory)
    }

  }
}
