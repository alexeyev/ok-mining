package ru.stachek66.okminer.corpus

import java.io.{FileWriter, BufferedWriter, File, IOException}
import java.net.URL
import ru.stachek66.okminer.cleaning.StructuredExtractor
import ru.stachek66.okminer.model.Community
import scala.collection.JavaConversions._

/**
 * TODO: decomposition!!!
 * @author alexeyev
 */
object LoaderScript {

  // one link per line
  private val linksSource = new File("links.txt")
  require(linksSource.exists() && linksSource.isFile)

  /**
   * Suggested conversion for putting to text file.
   */
  def communityToCorpusEntity(community: Community): String =
    List(
      community.description,
      community.posts,
      community.linkedTexts.map(_.mkString(" ")).getOrElse("")).
      mkString(" ")

  def buildCommunity(se: StructuredExtractor) =
    Community(
      se.getDescription,
      se.getConcatenatedPosts,
      se.getLinksFromPosts,
      Some(se.getLinkedTexts),
      None)

  def main(args: Array[String]) {
    var counter = 0
    io.Source.fromFile(linksSource).getLines().foreach {
      link =>
        try {
          val se: StructuredExtractor = new StructuredExtractor(new URL(link))
          val writer =
            new BufferedWriter(
              new FileWriter(
                ru.stachek66.okminer.corpus.directory.getName + "/" +
                  counter + ".txt"))
          writer.write(
            communityToCorpusEntity(
              buildCommunity(se)))
          writer.close()
          counter += 1
        }
        catch {
          case e: IOException => {
            e.printStackTrace()
          }
        }
    }
  }
}
