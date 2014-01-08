package ru.stachek66.okminer.wiki

import org.apache.lucene.document.{Field, TextField, Document}
import org.apache.lucene.index.IndexWriter
import org.slf4j.LoggerFactory

/**
 * @author alexeyev
 */
class WikiIndexer {

  private val log = LoggerFactory.getLogger(this.getClass)

  private def addToIndex(iw: IndexWriter, title: String, text: String) {
    val doc = new Document()
    doc.add(new TextField(IndexProperties.titleField, title, Field.Store.YES))
    doc.add(new TextField(IndexProperties.textField, text, Field.Store.YES))
    iw.addDocument(doc)
  }

  private def fillIndex() {
    val iw = new IndexWriter(IndexProperties.index, IndexProperties.config)
    new WikiTextAndLinkFetcher().fetch {
      case (title, text, links) =>
          addToIndex(iw, title, text)
    }
    iw.close()
  }

  def doIndex() {
    log.info("Starting indexing.")
    fillIndex()
  }
}
