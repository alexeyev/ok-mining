package ru.stachek66.okminer

import java.io.File

/**
 * @author alexeyev
 */
package object wiki {
  //
  val dumps = Map(
    "ru" -> new File("../ruwiki-latest-pages-meta-current.xml"),
    "en" -> new File("../enwiki-latest-pages-meta-current.xml")
  )

  //  val dump = new File("../test_dump.xml")
}
