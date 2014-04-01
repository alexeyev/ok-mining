package ru.stachek66.okminer

import java.io.File
import ner.tree.InvertedRadixTreeNER
import org.slf4j.LoggerFactory
import utils.{StatsFileIO, CounterLogger}
import utils.storage.Storage

/**
 * A tool for processing the whole corpus:
 * it reads from files and writes to files provided
 * with necessary directories.
 * @author alexeyev
 */
object CorpusProcessorWriter {

  private val processor = new OneYearProcessor(ner = new InvertedRadixTreeNER())
  private val yearPattern = "\\d{1,4}".r.pattern
  private val clog = new CounterLogger(LoggerFactory.getLogger("corpus-global-processed"), 100, "%s files processed")

  /**
   * Writing the results of the whole corpus processing to the specified folder.
   * @param corpus corpus directory; it should contain folders with names matching "\\d{1,4}" with texts in them
   * @param reportsDirectory directory for writing tsv reports into
   */
  def processCorpus(corpus: File, reportsDirectory: File) {

    reportsDirectory.mkdirs()

    Storage.withDao {
      dao => {
        clog.getLogger.info("Dao opened.")
        for {
        // choosing appropriate directories
          directory <- corpus.listFiles().toIterable
          if directory.isDirectory &&
            yearPattern.matcher(directory.getName).matches() &&
            directory.listFiles().nonEmpty
        } {
          val log = new CounterLogger(LoggerFactory.getLogger(directory.getName + "-processor"), 10, "%s files processed")
          log.getLogger.info("I'm parsing " + directory.getName)
          // carrying out the core task
          //            val data = processor.extractFromYearDirectory(directory, List(log, clog))
          processor.flushFromYearDirectory(directory, List(log, clog), dao)
          // writing everything down
          val data = dao.getStats(directory.getName.toInt)
          StatsFileIO.writeToFile(data, new File(s"${reportsDirectory.getAbsolutePath}/${directory.getName}.tsv"))
          log.getLogger.info("Done with it!")
        }
      }
    }
  }
}