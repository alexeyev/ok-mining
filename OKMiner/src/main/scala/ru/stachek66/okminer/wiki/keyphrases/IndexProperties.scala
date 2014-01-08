package ru.stachek66.okminer.wiki.keyphrases

import java.io.File
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.NIOFSDirectory
import ru.stachek66.okminer.Meta

/**
  * @author alexeyev
  */
object IndexProperties {
   //todo: rewrite
   val textField = "text"

   val analyzer: Analyzer = new RussianAnalyzer(Meta.luceneVersion)
   val index = new NIOFSDirectory(new File("wiki_keyphrases_index"))
   val config = new IndexWriterConfig(Meta.luceneVersion, analyzer)
 }
