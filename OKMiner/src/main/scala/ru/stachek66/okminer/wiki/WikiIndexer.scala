package ru.stachek66.okminer.wiki

import java.io.File
import org.apache.lucene.document.{Field, TextField, Document}
import org.apache.lucene.index.IndexWriter
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.Lexer

/**
 * @author alexeyev
 */
object WikiIndexer {

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
        log.info(links.toString())
    }
    iw.close()
  }

  def main(args: Array[String]) {
    fillIndex()
  }
}
