package ru.stachek66.okminer.model

import ru.stachek66.okminer.cleaning.StructuredExtractor
import scala.collection.JavaConversions._

/**
 * @author alexeyev
 */
object Conversions {


  def extractorToCommunity(se: StructuredExtractor) =
    Community(
      se.getDescription,
      se.getConcatenatedPosts,
      se.getLinksFromPosts,
      Some(se.getLinkedTexts),
      None)

  def communityToCorpusEntity(community: Community): String =
    List(
      community.description, community.description,
      community.posts,
      community.linkedTexts.map(_.mkString(" ")).getOrElse("")
    ).mkString(" ")
}
