package ru.stachek66.okminer

/**
 * @author alexeyev
 */
package object ner {

  val sources = Iterable(
    classOf[ClassLoader].getResourceAsStream("/habrahabr-companies.tsv"),
    classOf[ClassLoader].getResourceAsStream("/crunchbase-companies.tsv")
  )

  def accessSources(handler: String => Unit) {
    for (stream <- sources) {
      io.Source.fromInputStream(stream)("UTF-8").getLines().foreach(handler(_))
    }
  }
}

