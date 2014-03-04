package ru.stachek66.okminer.wiki.keyphrases

import java.io.File
import org.apache.lucene.document.{Field, TextField, Document}
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.store.NIOFSDirectory
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.wiki.IndexHolder
import ru.stachek66.okminer.wiki.fetchers.LinksFetcher

/**
 * @author alexeyev
 */
class Index extends IndexHolder {

  private val log = LoggerFactory.getLogger(this.getClass)

  protected override val indexDir = {
    val path = new File("indices/wiki_keyphrases_index_short")
    path.mkdirs()
    new NIOFSDirectory(path)
  }

  //  def safeAccessIndex: org.apache.lucene.store.Directory = Try {
  //    IndexReader.open(innerIndexDir)
  //  } match {
  //    case Failure(_) => {
  //      log.error("Index not found.")
  //      doIndex()
  //      innerIndexDir
  //    }
  //    case Success(_) => {
  //      log.info("Index found.")
  //      innerIndexDir
  //    }
  //  }

  private def addToIndex(iw: IndexWriter, words: String) {
    val doc = new Document()
    doc.add(new TextField(IndexProperties.textField, words, Field.Store.YES))
    iw.addDocument(doc)
  }

  private def fillIndex() {
    val iw = new IndexWriter(indexDir, IndexProperties.config)
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

  protected override def doIndex() {
    log.info("Starting indexing links.")
    fillIndex()
    log.info("Indexing links done.")
  }

}