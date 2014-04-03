package ru.stachek66.okminer

import java.io.InputStream

/**
 * Common objects for NER
 */
package object ner {

  /**
   * All raw company names' stream.
   */
  private def sources: Iterable[InputStream] = Iterable(
    //    NormalizedHabrahabrAccessor.getNormalizedStream,
    classOf[ClassLoader].getResourceAsStream("/habrahabr-companies.tsv"),
    classOf[ClassLoader].getResourceAsStream("/crunchbase-companies.tsv")
  )

  /**
   * Providing access to companies' sources.
   * @param handler a function doing something with every company's name
   */
  def accessSources(handler: String => Unit) {
    for (stream <- sources) {
      io.Source.fromInputStream(stream)("UTF-8").getLines().foreach(line => handler(line.trim))
    }
  }
}

