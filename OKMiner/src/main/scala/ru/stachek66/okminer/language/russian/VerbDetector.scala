package ru.stachek66.okminer.language.russian

/**
 * A simple regex tool checking if a given word is a verb.
 * Please mind the danger of probable false negatives.
 * @author alexeyev
 */
object VerbDetector {

  private val regex = ".*((ал|ял|ил|нул|ел)(а|о)?|(у|и|е|я|ю)(шь|ть|т))(cь|ся)?".r.pattern

  def isVerb(word: String): Boolean = regex.matcher(word).matches()

}
