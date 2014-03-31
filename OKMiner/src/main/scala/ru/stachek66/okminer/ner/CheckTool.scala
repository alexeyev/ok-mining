package ru.stachek66.okminer.ner


/**
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

  val companies = new NaiveNER(new Searcher).extractAllCompanies(description)
  companies.foreach(println)
  //
  //  val n = new NaiveNER(new Searcher)//.extractAllCompanies("Apple Google Yandex")
  //  val e = n.extractAllCompanies("Apple")
  //  println("extracted:", e)
}