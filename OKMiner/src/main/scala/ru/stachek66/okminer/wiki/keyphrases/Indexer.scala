package ru.stachek66.okminer.wiki.keyphrases

import org.apache.lucene.document.{Field, TextField, Document}
import org.apache.lucene.index.IndexWriter
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.wiki.fetchers.LinksFetcher

/**
 * @author alexeyev
 */
class Indexer {
  //todo: make safe

  private val log = LoggerFactory.getLogger(this.getClass)

  private def addToIndex(iw: IndexWriter, words: String) {
    val doc = new Document()
    doc.add(new TextField(IndexProperties.textField, words, Field.Store.YES))
    iw.addDocument(doc)
  }

  private def fillIndex() {
    val iw = new IndexWriter(IndexProperties.index, IndexProperties.config)
    new LinksFetcher().fetch {
      case links =>
        links.foreach {
          link => {
            addToIndex(iw, link)
            addToIndex(iw, "ek42")
          }
        }
    }
    iw.commit()
    iw.close()
  }

  def doIndex() {
    log.info("Starting indexing links.")
    fillIndex()
    log.info("Indexing links done.")
  }
}
