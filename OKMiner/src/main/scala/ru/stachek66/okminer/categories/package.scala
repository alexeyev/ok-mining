package ru.stachek66.okminer

import java.io.File

/**
 * Configurable data, shared variables for references
 * @author alexeyev
 */
package object categories {

  val dump =  classOf[ClassLoader].getResourceAsStream("/wikiGraph.txt.gz")
  val parsed = new File("parsed/categories.tsv")

}
