import java.io.FileWriter
import ru.stachek66.okminer.language.russian.StopWordsFilter

/**
 * If necessary, can be used for cleaning stopwords list from
 * pipes and comments.
 * @author alexeyev
 */
object StopListConversionTool {

  def main(args: Array[String]) {

    val list = StopWordsFilter.stopList
    println(list)

    val out = new FileWriter("tools/clean_stopwords.txt")

    for (stopWord <- list) {
      out.write("%s\n" format stopWord)
    }

    out.close()
  }
}