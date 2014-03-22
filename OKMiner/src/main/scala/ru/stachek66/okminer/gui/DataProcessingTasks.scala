package ru.stachek66.okminer.gui

import java.io.File
import java.security.InvalidParameterException

/**
 * All important calls for computing are provided by this class.
 * @author alexeyev
 */
private[gui] object DataProcessingTasks {

  /**
   * Building *.tsv-reports by corpus
   * @param src  corpus
   * @param dest reports directory
   */
  def buildReports(src: Option[File], dest: Option[File]) {
    if (src.isEmpty) throw new InvalidParameterException("Corpus directory unset")
    if (dest.isEmpty) throw new InvalidParameterException("Destination directory unset")
    //todo:
  }

  /**
   * Drawing yearly graphs provided with reports
   *
   * @param reports reports directory
   */
  def drawGraphs(reports: Option[File]) {
    if (reports.isEmpty) throw new InvalidParameterException("Destination directory unset")
    //todo:
  }

}
