package ru.stachek66.okminer.wiki.vocabulary

import ru.stachek66.okminer.wiki.WikiVisitor
import ru.stachek66.okminer.language.russian.Tokenizer
import scala.collection.JavaConversions._
import java.io.{File, FileWriter}

/**
 * @author alexeyev
 */
object Vocabulary extends App {

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
    val voc = collection.mutable.Set[String]()
    new WikiVisitor().visit(
      page =>
        if (!page.isRedirect && !page.isSpecialPage && !page.isDisambiguationPage) {
          (Tokenizer.tokenize(page.getTitle) ++
            page.getLinks.flatMap(Tokenizer.tokenize(_))).foreach(voc.add(_))
        }
    )
    voc.toSet
  }

  private def flush() {
    val fw = new FileWriter("tools/vocabulary.txt")
    normalizedWords.foreach {
      token =>
        fw.write("%s\n".format(token))
    }
    fw.close()
  }

  flush()
}
