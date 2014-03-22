package ru.stachek66.okminer

import java.io.File

/**
 * All wikisources paths.
 * @author alexeyev
 */
package object wiki {
  val dumps = Map(
    "ru" -> new File("../meta_data/ruwiki-latest-pages-meta-current.xml"),
    "en" -> new File("../meta_data/enwiki-latest-pages-meta-current.xml")
  )
}
