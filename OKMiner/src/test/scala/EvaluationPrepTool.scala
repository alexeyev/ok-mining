import java.io.{FileWriter, File}
import ru.stachek66.okminer.ner.tree.InvertedRadixTreeNER
import ru.stachek66.okminer.utils.FileUtils

/**
 * @author alexeyev
 */
object EvaluationPrepTool extends App {

  val dir = new File("../corpus-test/clean/2014/")

  println(dir.isDirectory)

  val fw = new FileWriter("../../Desktop/ner-eval2.tsv")

  val ner = new InvertedRadixTreeNER

  var counter = 0

  for {
    file <- dir.listFiles()
    text = FileUtils.asStringWithoutNewLines(file)
    nered = ner.extractAllCompanies(text)
  } if (counter <= 200 && text.size < 2000 && text.size > 50) {
    counter += 1
    fw.write(text + "\t" + nered.mkString(",") + "\n")
  }

  fw.close()
}
