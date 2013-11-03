package ru.stachek66.okminer.corpus

import ru.stachek66.okminer.language.{RussianStemmer, RussianTokenizer}
import ru.stachek66.okminer.utils.FileUtils

/**
 * @author alexeyev
 */
object FreqDict {

  import RussianStemmer._
  import RussianTokenizer._

  lazy val bags: Set[String] = {
    directory.
      listFiles().
      filter(_.isFile).
      map {
      file => {
        stem(tokenize(FileUtils.slurpFile(file))).toSet
      }
    }
  }
}
