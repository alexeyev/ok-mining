package ru.stachek66.okminer.wiki.translation.tools

import java.io.FileWriter

/**
 *
 * @author alexeyev
 */
object Tool extends App {

  val fw = new FileWriter("parsed/ru-en-titles.tsv")

  val ruIdToEnTitle = LangLinksSQLParser.idToEnTitle

  PageSQLParser.parseDump(PageSQLParser.rudump, "utf8") {
    (id, title) => {
      if (ruIdToEnTitle.keySet.contains(id)) {
        fw.write("%s\t%s\n".format(title, ruIdToEnTitle(id)))
      }
    }
  }

  val enIdToRuTitle = LangLinksSQLParser.idToRuTitle

  PageSQLParser.parseDump(PageSQLParser.endump, "utf8") {
    (id, title) => {
      if (enIdToRuTitle.keySet.contains(id)) {
        fw.write("%s\t%s\n".format(enIdToRuTitle(id), title))
      }
    }
  }

  fw.close()
}