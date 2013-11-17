package ru.stachek66.okminer.corpus

import java.io.{BufferedWriter, FileWriter, File}
import org.apache.commons.collections.bag.HashBag
import ru.stachek66.okminer.language.russian.StopWordsFilter.{filter => stopFilter}
import ru.stachek66.okminer.language.russian.Tokenizer._
import ru.stachek66.okminer.utils.FileUtils
import scala.collection.JavaConversions._

/**
 * @author alexeyev
 */
object PreprocessorScript {

  def main(args: Array[String]) {
    tfIdfDirectory.mkdirs()

    rawCorpusDirectory.
      listFiles().
      filter(_.isFile).
      map {
      file: File => {
        println("Processing " + file.getName)
        val wellPreparedTokens =
        //stem(
          stopFilter(
            tokenize(
              FileUtils.asStringWithoutNewLines(file))) //)
        // ain't a nice piece of code
        // yet faster than groupby
        val bag = new HashBag()
        for (t <- wellPreparedTokens) {
          bag.add(t)
        }

        val fw = new BufferedWriter(new FileWriter(tfIdfDirectory.getName + "/" + file.getName))
        for (key <- bag.uniqueSet().map(_.asInstanceOf[String]).toSet[String]) {
          fw.write("%s\t%d\n" format(key, bag.getCount(key)))
        }
        fw.close()
        println()
      }
    }
  }
}
