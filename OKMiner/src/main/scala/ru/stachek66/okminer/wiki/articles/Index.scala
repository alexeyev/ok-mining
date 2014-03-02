package ru.stachek66.okminer.wiki.articles

import org.apache.lucene.document.{Field, TextField, Document}
import org.apache.lucene.index.{IndexReader, IndexWriter}
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.wiki.fetchers.TextFetcher
import scala.util.{Success, Failure, Try}
import org.apache.lucene.store.NIOFSDirectory
import java.io.File

/**
 * @author alexeyev
 */
class Index {
  //todo: make safe

  private val log = LoggerFactory.getLogger(this.getClass)

  private val indexDir = {
    val path = new File("indices/wiki_index")
    path.mkdirs()
    new NIOFSDirectory(path)
  }

  val index = Try {
    IndexReader.open(indexDir)
  } match {
    case Failure(_) => {
      doIndex()
      indexDir
    }
    case Success(_) => {
      log.info("Index found")
      indexDir
    }
  }

  private def addToIndex(iw: IndexWriter, title: String, text: String) {
    val doc = new Document()
    doc.add(new TextField(IndexProperties.titleField, title, Field.Store.YES))
    doc.add(new TextField(IndexProperties.textField, text, Field.Store.YES))
//    doc.add(new TextField(IndexProperties.idField, text, Field.Store.YES))
    iw.addDocument(doc)
  }

  private def fillIndex() {
    log.info("Filling...")
    val iw = new IndexWriter(indexDir, IndexProperties.config)
    new TextFetcher().fetch {
      case (title, text) =>
        addToIndex(iw, title, text)
    }
    iw.commit()
    iw.close()
  }

  private def doIndex() {
    log.info("Starting indexing.")
    indexDir.getDirectory.listFiles().foreach(_.delete())
    fillIndex()
    log.info("Done.")
  }
}

object Index extends App {
  new Index()
}