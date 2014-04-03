package ru.stachek66.okminer.language.russian

/**
 * Stoplist taken from Snowball project + extended.
 * http://snowball.tartarus.org/algorithms/russian/stop.txt
 *
 * BTW, "ё" is always replaced by "е" due to the usage of this stoplist.
 *
 * Please note: should be applied after tokenization.
 *
 * @author alexeyev
 */
object StopWordsFilter {

  /**
   * An immutable list of stopwords.
   */
  lazy val stopList: Set[String] =
    io.Source.fromInputStream(
      classOf[ClassLoader].getResourceAsStream("/stopwords.txt")
    )("UTF-8").getLines().map {
      line =>
        line.split("\\|")(0).trim
    }.toSet


  private val numberPattern = "\\d+".r.pattern

  /**
   * Filtering out 1-letter words, stopwords and numbers.
   * @param words input words
   * @return filtered list
   */
  def filter(words: Iterable[String]): Iterable[String] = {
    words.map(_.replace("ё", "е")).
      filter(word =>
      word.length > 1 &&
        !numberPattern.matcher(word).matches() &&
        !stopList.contains(word.toLowerCase))
  }
}
