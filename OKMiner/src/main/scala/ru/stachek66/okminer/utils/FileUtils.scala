package ru.stachek66.okminer.utils

import java.io.File

/**
 * Tools for common tasks for working with text files
 * @author alexeyev
 */
object FileUtils {

  /**
   * Turns newlines into spaces
   * @param file target file
   * @return resulting string
   */
  def asStringWithoutNewLines(file: File) = io.Source.fromFile(file)("UTF-8").getLines().mkString(" ")

  /**
   * Returns file as string without any modifications
   * @param file target file
   * @return resulting string
   */
  def asString(file: File) = io.Source.fromFile(file)("UTF-8").getLines().mkString("\n")

}
