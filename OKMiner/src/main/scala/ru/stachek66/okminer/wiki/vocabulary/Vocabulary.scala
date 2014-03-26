package ru.stachek66.okminer.wiki.vocabulary

import java.io.FileWriter
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.Tokenizer
import ru.stachek66.okminer.utils.CounterLogger
import ru.stachek66.okminer.wiki.fetchers.WikiVisitor
import ru.stachek66.okminer.wiki.utils._
import scala.collection.JavaConversions._

/**
 * All normalized words that are allowed to be analyzed.
 * Originally got from Wikipedia.
 * @author alexeyev
 */
object Vocabulary {

  private val log = LoggerFactory.getLogger("voc")
  private val clog = new CounterLogger(log, 10000, "%s items processed")
  private lazy val vocAsTxtFile = ru.stachek66.okminer.wiki.parsed("voc")

  lazy val normalizedWords: Set[String] = {
    if (vocAsTxtFile.exists() && io.Source.fromFile(vocAsTxtFile).getLines().nonEmpty) getFromTxt
    else {
      val vocabulary = getFromDump
      flush(vocabulary)
      vocabulary
    }
  }

  private def getFromTxt = io.Source.fromFile(vocAsTxtFile).getLines().map(_.trim).toSet

  /**
   * Putting all titles and links fron wiki dump into the vocabulary in normalized form.
   * It is expected that vocabulary fits into RAM.
   */
  private def getFromDump = {
    def normalize(s: String) = Tokenizer.tokenize(s.replace("_", " "))

    val numberPattern = "\\d+".r.pattern
    val voc = collection.mutable.Set[String]()
    new WikiVisitor(clog).visit(
      page =>
        if (Helper.isCoolPage(page)) {
          for {
            token <- (normalize(page.getTitle) ++ page.getLinks.flatMap(l => normalize(l)))
            if !numberPattern.matcher(token).matches()
          } {
            voc.add(token)
          }
        }
    )
    voc.toSet
  }

  private def flush(data: Set[String]) {
    val fw = new FileWriter(vocAsTxtFile)
    data.foreach {
      token =>
        fw.write("%s\n".format(token))
    }
    fw.close()
    log.info("Flushing done.")
  }

  def main(args: Array[String]) {
    log.info("Checking if vocabulary is present")
    println(normalizedWords)
  }
}
