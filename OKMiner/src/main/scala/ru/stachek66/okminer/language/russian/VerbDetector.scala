package ru.stachek66.okminer.language.russian

/**
 * A lot of false negatives :(
 * @author alexeyev
 */
object VerbDetector {

  private val regex = ".*((ал|ял|ил|нул|ел)(а|о)?|(у|и|е|я|ю)(шь|ть|т))(cь|ся)?".r.pattern

  def isVerb(word: String): Boolean = regex.matcher(word).matches()

  def main(args: Array[String]) {
    assert(isVerb("свистит") == true)
    assert(isVerb("горят") == true)
    assert(isVerb("молятся") == true)
//    assert(isVerb("ринит") == false)
  }

}
