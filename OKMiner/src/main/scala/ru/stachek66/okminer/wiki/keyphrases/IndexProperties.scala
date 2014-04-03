package ru.stachek66.okminer.wiki.keyphrases

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.apache.lucene.index.IndexWriterConfig
import ru.stachek66.okminer.Meta

object IndexProperties {
  val textField = "text"

  val analyzer: Analyzer = new RussianAnalyzer(Meta.luceneVersion)
  val index = new Index()
  val config = new IndexWriterConfig(Meta.luceneVersion, analyzer)
}
