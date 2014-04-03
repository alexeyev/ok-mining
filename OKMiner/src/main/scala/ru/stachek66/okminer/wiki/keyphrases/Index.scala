package ru.stachek66.okminer.wiki.keyphrases

import org.apache.lucene.document.{Field, TextField, Document}
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.store.NIOFSDirectory
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.wiki.IndexHolder
import ru.stachek66.okminer.wiki.fetchers.LinksFetcher

/**
 * Index over wiki-links and titles
 * @author alexeyev
 */
class Index extends IndexHolder {

  private val log = LoggerFactory.getLogger(this.getClass)

  protected override val indexDir = {
    val path = ru.stachek66.okminer.wiki.indices("ru_wiki_links")
    path.mkdirs()
    new NIOFSDirectory(path)
  }

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

/**
 * Index building
 */
object Index {
  def main(args: Array[String]) {
    new Index().doIndex()
  }
}
