package ru.stachek66.okminer.corpus

import java.io.{BufferedWriter, FileWriter, File}
import ru.stachek66.okminer.language.russian.Stemmer._
import ru.stachek66.okminer.language.russian.StopWordsFilter.{filter => stopFilter}
import ru.stachek66.okminer.language.russian.Tokenizer._
import ru.stachek66.okminer.utils.FileUtils

/**
 * @author alexeyev
 */
object PreprocessorScript {

  def main(args: Array[String]) {
    rawCorpusDirectory.
      listFiles().
      filter(_.isFile).
      foreach {
      file: File => {
        println("Processing " + file.getName)
        stem(
          stopFilter(
            tokenize(
              FileUtils.asStringWithoutNewLines(file)))).
          // counting frequencies
          groupBy(token => {
          println("grouping " + token)
          token
        }).
          foreach {
          case (token, list) => {
            println("writing")
            val fw = new BufferedWriter(new FileWriter(tfIdfDirectory.getName + "/" + file.getName))
            fw.write("%s\t%d\n" format(token, list.size))
            fw.close()
          }
        }
      }
    }
  }

}
