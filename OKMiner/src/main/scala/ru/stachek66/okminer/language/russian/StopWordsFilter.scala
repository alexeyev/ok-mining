package ru.stachek66.okminer.language.russian

/**
 * Should be applied after tokenization.
 *
 * Stoplist taken from Snowball project.
 * http://snowball.tartarus.org/algorithms/russian/stop.txt
 *
 * BTW, "ё" is always replaced by "е" due to the usage of this stoplist.
 * @author alexeyev
 */
object StopWordsFilter {

  private lazy val stopList: Set[String] =
    io.Source.fromInputStream(
      classOf[ClassLoader].getResourceAsStream("/stopwords.txt")
    )("UTF-8").getLines().map {
      line =>
        line.split("\\|")(0).trim
    }.toSet

  @deprecated
  def getList = stopList

  def filter(words: Iterable[String]): Iterable[String] = {
    words.map(_.replace("ё", "е")).
      filter(word => word.length > 2 && !word.matches("\\d+") && !stopList.contains(word.toLowerCase))
  }
}
