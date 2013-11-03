package ru.stachek66.okminer.utils

import java.io.File

/**
 * @author alexeyev
 */
object FileUtils {

  def slurpFile(file: File) =
    io.Source.fromFile(file).getLines().mkString(" ")

}
