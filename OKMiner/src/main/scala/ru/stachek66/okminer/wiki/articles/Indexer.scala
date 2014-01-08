package ru.stachek66.okminer.wiki.articles

import org.apache.lucene.document.{Field, TextField, Document}
import org.apache.lucene.index.IndexWriter
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.wiki.fetchers.TextFetcher

/**
 * @author alexeyev
 */
class Indexer {
  //todo: make safe

  private val log = LoggerFactory.getLogger(this.getClass)

  private def addToIndex(iw: IndexWriter, title: String, text: String, id: String) {
    val doc = new Document()
    doc.add(new TextField(IndexProperties.titleField, title, Field.Store.YES))
    doc.add(new TextField(IndexProperties.textField, text, Field.Store.YES))
    doc.add(new TextField(IndexProperties.idField, text, Field.Store.YES))
    iw.addDocument(doc)
  }

  private def fillIndex() {
    val iw = new IndexWriter(IndexProperties.index, IndexProperties.config)
    new TextFetcher().fetch {
      case (title, text) =>
          addToIndex(iw, title, text, "0")
    }
    iw.close()
  }

  def doIndex() {
    log.info("Starting indexing.")
    fillIndex()
  }
}
