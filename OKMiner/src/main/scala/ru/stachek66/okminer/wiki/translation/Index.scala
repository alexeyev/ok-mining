package ru.stachek66.okminer.wiki.translation

import java.io.File
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.apache.lucene.document.{Field, TextField, Document}
import org.apache.lucene.index.{IndexReader, IndexWriterConfig, IndexWriter}
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.NIOFSDirectory
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.Meta
import ru.stachek66.okminer.utils.CounterLogger
import ru.stachek66.okminer.wiki._


class Index(mapFile: File = parsed("ru_en"), destDirectory: File = indices("ru_en")) {

  import IndexProperties._

  destDirectory.mkdirs()
  mapFile.getParentFile.mkdirs()

  private def addToIndex(iw: IndexWriter, pair: RuEn) {
    val doc = new Document()
    doc.add(new TextField(ru, pair._1, Field.Store.YES))
    doc.add(new TextField(en, pair._2, Field.Store.YES))
    iw.addDocument(doc)
  }

  private val log = LoggerFactory.getLogger("lang-index")
  private val clog = new CounterLogger(log, 100000, "%s pairs processed")
  val analyzer: Analyzer = new RussianAnalyzer(Meta.luceneVersion)

  private val indexDir = new NIOFSDirectory(destDirectory)

  private def openReader(attempts: Int = 3): IndexReader =
    try {
      IndexReader.open(indexDir)
    } catch {
      case e: Exception => {
        log.info("Index not found.")
        if (attempts <= 1) {
          log.error("Can't refill or open index")
          throw new Exception("Index corrupted")
        }
        log.info("Reattempting...")
        fillIndex(mapFile)
        openReader(attempts - 1)
      }
      case ir: IndexReader => {
        log.info("Index found.")
        ir
      }
    }

  private val (reader, writer, searcher) = {
    val r = openReader()
    (r,
      new IndexWriter(indexDir, new IndexWriterConfig(Meta.luceneVersion, analyzer)),
      new IndexSearcher(r))
  }

  def withSearcher[T](action: IndexSearcher => T): T = action(searcher)

  def withWriter(action: IndexWriter => Unit) {
    action(writer)
  }

  private def fillIndex(file: File) {
    log.info("Filling lang index...")
    log.info("Destination: " + indexDir.getDirectory)

    withWriter {
      iw => {
        io.Source.fromFile(file).getLines().
          foreach {
          line => clog.execute {
            val splitted = line.trim.split("\t")
            addToIndex(iw, (splitted(0), splitted(1)))
          }
        }
        iw.commit()
      }
    }
    log.info("Done.")
  }
}

object Index {
  def main(args: Array[String]) {
    new Index()
  }
}
