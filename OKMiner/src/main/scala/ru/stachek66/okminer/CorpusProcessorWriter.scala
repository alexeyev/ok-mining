package ru.stachek66.okminer

import java.io.File
import ner.tree.InvertedRadixTreeNER
import org.slf4j.LoggerFactory
import utils.{StatsFileIO, CounterLogger}
import utils.storage.StoredBag
import scala._

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
  def processCorpus(corpus: File, reportsDirectory: File, bag: Option[StoredBag] = None) {

    reportsDirectory.mkdirs()

    def doProcessing(getData: (File, List[CounterLogger]) => TraversableOnce[(String, String, Int)]) {
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
        val data = getData(directory, List(log, clog))
        StatsFileIO.writeToFile(data, new File(s"${reportsDirectory.getAbsolutePath}/${directory.getName}.tsv"))
        log.getLogger.info("Done with it!")
      }
    }

    bag match {
      case None => doProcessing {
        //doing it all in memory
        case (directory, loggers) => processor.extractFromYearDirectory(directory, loggers)
      }
      case Some(storedBag) =>
        storedBag.withDao {
          dao =>
          // flushing results after reading each file
            doProcessing {
              case (directory, loggers) => {
                processor.flushFromYearDirectory(directory, loggers, dao)
                dao.getStats(directory.getName.toInt)
              }
            }
        }
    }
  }
}