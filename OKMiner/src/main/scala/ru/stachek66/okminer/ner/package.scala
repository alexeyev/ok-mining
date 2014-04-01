package ru.stachek66.okminer

import utils.HabrahabrAccessor

/**
 * @author alexeyev
 */
package object ner {

  val sources = Iterable(
    HabrahabrAccessor.getNormalizedStream,
    classOf[ClassLoader].getResourceAsStream("/crunchbase-companies.tsv")
  )

  def accessSources(handler: String => Unit) {
    for (stream <- sources) {
      io.Source.fromInputStream(stream)("UTF-8").getLines().foreach(handler(_))
    }
  }
}

