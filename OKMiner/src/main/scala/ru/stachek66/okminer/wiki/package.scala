package ru.stachek66.okminer

import java.io.File

/**
 * All wikisources paths + all indices paths.
 * @author alexeyev
 */
package object wiki {

  val dumps = Map(
    "ru" -> new File("../meta_data/ruwiki-latest-pages-meta-current.xml.gz"),
    //    "en" -> new File("../meta_data/enwiki-latest-pages-meta-current.xml"),
    "en" -> new File("../meta_data/enwiki-all.xml.gz"),
    "ru_langlinks" -> new File("../meta_data/ruwiki-latest-langlinks.sql.gz"),
    "en_langlinks" -> new File("../meta_data/enwiki-latest-langlinks.sql.gz")
  )

  val indices = Map(
    "ru_wiki_titles_and_texts" -> new File("indices/wiki_index"),
    "ru_wiki_links" -> new File("indices/wiki_keyphrases_index_short"),
    "ru_en" -> new File("indices/lang_index")
  )

  val parsed = Map(
    "ru_en" -> new File("parsed/ru-en-sorted.tsv")
  )
}
