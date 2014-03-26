package ru.stachek66.okminer.ner

import java.io.Closeable
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.ru.RussianAnalyzer
import ru.stachek66.okminer.Meta
import org.apache.lucene.index.{IndexReader, IndexWriter, IndexWriterConfig}
import org.apache.lucene.search.IndexSearcher

/**
 * @author alexeyev
 */

class RAMIndex() extends Closeable {
  private val dir = new RAMDirectory()
  val analyzer: Analyzer = new RussianAnalyzer(Meta.luceneVersion)
  private val config = new IndexWriterConfig(Meta.luceneVersion, analyzer)
  private val writer = new IndexWriter(dir, config)
  private val reader = IndexReader.open(dir)
  private val searcher = new IndexSearcher(reader)

  def withSearcher[T](action: IndexSearcher => T): T = action(searcher)

  def withWriter(action: IndexWriter => Unit) {
    action(writer)
  }

  def close() {
    writer.close()
    reader.close()
  }
}
