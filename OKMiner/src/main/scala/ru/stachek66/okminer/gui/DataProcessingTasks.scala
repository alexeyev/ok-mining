package ru.stachek66.okminer.gui

import java.io.File
import java.security.InvalidParameterException
import ru.stachek66.okminer.{CorpusProcessorWriter, GraphsTool}

/**
 * All important calls for computing are provided by this class.
 * @author alexeyev
 */
private[gui] object DataProcessingTasks {

  private val resultsFolderName = "results"
  private val graphsFolderName = "results"

  /**
   * Building *.tsv-reports by corpus
   * @param src  corpus
   * @param dest reports directory
   */
  def buildReports(src: Option[File], dest: Option[File]) {
    if (src.filter(_.isDirectory).isEmpty) throw new InvalidParameterException("Corpus directory unset")
    if (dest.filter(_.isDirectory).isEmpty) throw new InvalidParameterException("Destination directory unset")

    CorpusProcessorWriter.processCorpus(src.get, dest.get)

  }

  /**
   * Drawing yearly graphs provided with reports
   *
   * @param reports reports directory
   */
  def drawGraphs(reports: Option[File]) {
    val hasResultsFolder =
      reports.filter {
        case dir =>
          dir.listFiles().toIterable.filter {
            case subDir =>
              println(subDir, subDir.getName, subDir.getName.equals(resultsFolderName))
              subDir.isDirectory && subDir.getName.equals(resultsFolderName)
          }.nonEmpty
      }.nonEmpty
    if (!hasResultsFolder) throw new InvalidParameterException("Destination directory invalid")

    val src = new File(reports.get.getAbsolutePath + "/" + resultsFolderName)
    val dest = new File(reports.get.getAbsolutePath + "/" + graphsFolderName)
    GraphsTool.drawFromDirectory(src, dest)
  }

}
