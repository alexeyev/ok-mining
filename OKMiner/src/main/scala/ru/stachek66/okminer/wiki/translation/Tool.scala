package ru.stachek66.okminer.wiki.translation

import java.io.FileWriter

/**
 * @author alexeyev
 */
object Tool extends App {

  val fw = new FileWriter("../ru-en-titles.tsv")
  //oom danger
  val idToTitle = PageSQLParser.idToTitle
  for {
    (id, entitle) <- LangLinksSQLParser.idToEnTitle
  } {
    fw.write("%s\t%s\t%s\n".format(id, idToTitle(id), entitle))
  }
  fw.close()

}
