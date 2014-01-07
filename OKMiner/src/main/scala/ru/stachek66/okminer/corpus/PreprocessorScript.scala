package ru.stachek66.okminer.corpus

import java.io.FileWriter
import org.apache.commons.collections.bag.HashBag
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.StopWordsFilter.{filter => stopFilter}
import ru.stachek66.okminer.language.russian.Tokenizer._
import ru.stachek66.okminer.utils.FileUtils
import scala.collection.JavaConversions._

/**
 * @author alexeyev
 */
object PreprocessorScript {

  private val log = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]) {
    tfIdfDirectory.mkdirs()

    //todo: in parallel
    for {
      file <- rawCorpusDirectory.listFiles().toIterable
      if file.isFile
    } yield {
      log.info("Processing " + file.getName)
      val coolTokens =
        stopFilter(
          tokenize(
            FileUtils.asStringWithoutNewLines(file)
          )
        )

      // faster than using groupby
      val bag = new HashBag()
      for (t <- coolTokens) {
        bag.add(t)
      }

      val fw = new FileWriter(tfIdfDirectory.getName + "/" + file.getName)
      for (key <- bag.uniqueSet().map(_.asInstanceOf[String]).toSet[String]) {
        fw.write("%s\t%d\n" format(key, bag.getCount(key)))
      }
      fw.close()
    }
  }
}
