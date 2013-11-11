package ru.stachek66.okminer.utils

import java.io.File

/**
 * @author alexeyev
 */
object FileUtils {

  def asStringWithoutNewLines(file: File) =
    io.Source.fromFile(file).getLines().mkString(" ")

  def asString(file: File) =
    io.Source.fromFile(file).getLines().mkString("\n")

}
