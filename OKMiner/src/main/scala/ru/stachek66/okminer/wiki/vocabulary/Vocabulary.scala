package ru.stachek66.okminer.wiki.vocabulary

import ru.stachek66.okminer.wiki.WikiVisitor
import ru.stachek66.okminer.language.russian.Tokenizer
import scala.collection.JavaConversions._
import java.io.{File, FileWriter}
import org.slf4j.LoggerFactory

/**
 * All normalized words that are allowed to be analyzed.
 * @author alexeyev
 */
object Vocabulary extends App {

  private val log = LoggerFactory.getLogger("voc")

  lazy val normalizedWords: Set[String] = {
    val f = new File("tools/vocabulary.txt")
    if (f.exists()) {
      getFromTxt(f)
    } else {
      getFromDump
    }
  }

  private def getFromTxt(file: File) =
    io.Source.fromFile(file).getLines().map(_.trim).toSet

  private def getFromDump = {
    def heur(s: String) = s.replace("_", " ")

    val voc = collection.mutable.Set[String]()
    new WikiVisitor().visit(
      page =>
        if (!page.isRedirect && !page.isSpecialPage && !page.isDisambiguationPage) {
          (Tokenizer.tokenize(heur(page.getTitle)) ++
            page.getLinks.flatMap(t => Tokenizer.tokenize(heur(t)))).foreach(voc.add(_))
        }
    )
    voc.toSet
  }

  private def flush() {
    val f = new File("tools/vocabulary.txt")
    val fw = new FileWriter(f)
    normalizedWords.foreach {
      token =>
        fw.write("%s\n".format(token))
    }
    fw.close()
    log.info("Flushing done.")
  }
}
