package ru.stachek66.okminer.wiki.translation

import java.io.FileWriter

/**
 * @author alexeyev
 */
object Tool extends App {

  val fw = new FileWriter("../ru-en-titles.tsv")
  //oom danger
//  val idToTitle: Map[Long, String] = PageSQLParser.idToTitle
//
//  for {
//    (id: Long, enTitle) <- LangLinksSQLParser.idToEnTitle
//  } {
//    println("%s\t%s\t%s\n".format(id, idToTitle.get(id).get, enTitle))
//    fw.write("%s\t%s\t%s\n".format(id, idToTitle.get(id).get, enTitle))
//  }
//  println(PageSQLParser.ruIdToTitle)
  println(PageSQLParser.enIdToTitle)
//  println(LangLinksSQLParser.idToRuTitle.size)
//  println(LangLinksSQLParser.idToEnTitle.size)
  fw.close()
}