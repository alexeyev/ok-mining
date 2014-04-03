package ru.stachek66.okminer.ner

import indexing.{LuceneNER, Searcher}
import tree.InvertedRadixTreeNER


/**
 * Checking whether NERs work correctly.
 * @author alexeyev
 */
private object CheckTool extends App {

  //  val description = FileUtils.asString(new File("test/test.txt"))

  val description =
    "\"Ашманов и партнеры\" подготовили аналитический обзор \"Качество поиска и поисковые системы в Рунете\" за 2013 год. " +
      "В нем поиски Bing, Google, Mail, Yahoo и \"Яндекса\" сравниваются по 41 показателю. " +
      "Аутсайдерами чаще всего оказывались Bing и Yahoo, а русские поисковики приятно " +
      "радовали довольно высокими показателями. В частности, Mail.ru, в июле 2013 года " +
      "отказавшийся от поиска Google в пользу собственных технологий, " +
      "сделал солидный рывок вперед, догнав \"Яндекс\" по относительной полноте индекса. " +
      "Впрочем, на доле рынка поиска достижения Mail.ru заметным образом пока не отразились." +
      "  "

  // testing tree ner
  val companies = new InvertedRadixTreeNER().extractAllCompanies(description)
  companies.foreach(println)

  // testing lucene ner
  val n = new LuceneNER(new Searcher)
  val e = n.extractAllCompanies(description)
  println("extracted:" + e.mkString("\n"))
}