package ru.stachek66.okminer

import java.io.File

/**
 * @author alexeyev
 */
package object corpus {
  lazy val rawCorpusDirectory = new File("corpus/")
  lazy val tfIdfDirectory = new File("index/")
}
