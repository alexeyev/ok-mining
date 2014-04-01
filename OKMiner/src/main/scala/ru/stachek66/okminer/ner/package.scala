package ru.stachek66.okminer

package object ner {

  val sources = Iterable(
    //    NormalizedHabrahabrAccessor.getNormalizedStream,
    classOf[ClassLoader].getResourceAsStream("/habrahabr-companies.tsv"),
    classOf[ClassLoader].getResourceAsStream("/crunchbase-companies.tsv")
  )

  def accessSources(handler: String => Unit) {
    for (stream <- sources) {
      io.Source.fromInputStream(stream)("UTF-8").getLines().foreach(handler(_))
    }
  }
}

