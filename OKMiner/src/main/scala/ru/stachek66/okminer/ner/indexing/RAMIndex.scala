package ru.stachek66.okminer.ner.indexing

import java.io.Closeable
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.ru.RussianAnalyzer
import ru.stachek66.okminer.Meta
import org.apache.lucene.index.{IndexReader, IndexWriter, IndexWriterConfig}
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.document.{Field, TextField, Document}
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.ner._

/**
 * In-memory index interface for accessing companies.
 * @author alexeyev
 */
class RAMIndex extends Closeable {

  private val log = LoggerFactory.getLogger("ram-companies-index")

  private val dir = new RAMDirectory()
  val analyzer: Analyzer = new RussianAnalyzer(Meta.luceneVersion)
  private val config = new IndexWriterConfig(Meta.luceneVersion, analyzer)

  private def addToIndex(iw: IndexWriter, company: String) {
    val doc = new Document()
    doc.add(new TextField(IndexProperties.companyField, company, Field.Store.YES))
    iw.addDocument(doc)
  }

  {
    // making sure index is filled
    val iw = new IndexWriter(dir, config)
    log.info("Filling companies' index...")
    accessSources {
      line => addToIndex(iw, line.trim)
    }
    iw.commit()
    iw.close()
    log.info("Indexing done.")
  }

  private lazy val reader = IndexReader.open(dir)
  private val searcher = new IndexSearcher(reader)

  def withSearcher[T](action: IndexSearcher => T): T = {
    action(searcher)
  }

  def close() {
    reader.close()
  }
}