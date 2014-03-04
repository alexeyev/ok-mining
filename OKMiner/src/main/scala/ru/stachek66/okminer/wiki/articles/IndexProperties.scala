package ru.stachek66.okminer.wiki.articles

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.apache.lucene.index.IndexWriterConfig
import ru.stachek66.okminer.Meta

/**
 * @author alexeyev
 */
object IndexProperties {
  //todo: rewrite
  val textField = "text"
  val titleField = "title"
  val idField = "id"

  val analyzer: Analyzer = new RussianAnalyzer(Meta.luceneVersion)
  val config = new IndexWriterConfig(Meta.luceneVersion, analyzer)
  val index = new Index()

}