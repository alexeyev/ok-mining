package ru.stachek66.okminer.wiki

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.RAMDirectory
import ru.stachek66.okminer.Meta

/**
 * @author alexeyev
 */
object IndexProperties {
  //todo: rewrite
  val textField = "text"
  val titleField = "title"
  private val analyzer: Analyzer = new RussianAnalyzer(Meta.luceneVersion)
  val index = new RAMDirectory()
  val config = new IndexWriterConfig(Meta.luceneVersion, analyzer)
}
