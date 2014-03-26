package ru.stachek66.okminer.wiki.translation

import java.io.File
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.apache.lucene.document.{Field, TextField, Document}
import org.apache.lucene.index.{IndexReader, IndexWriterConfig, IndexWriter}
import org.apache.lucene.store.NIOFSDirectory
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.Meta
import ru.stachek66.okminer.utils.CounterLogger
import ru.stachek66.okminer.wiki._
import scala.util.{Success, Failure, Try}


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

  val index = Try {
    IndexReader.open(indexDir)
  } match {
    case Failure(_) => {
      log.info("Index not found.")
      fillIndex(mapFile)
      indexDir
    }
    case Success(_) => {
      log.info("Index found.")
      indexDir
    }
  }

  private def fillIndex(file: File) {
    log.info("Filling lang index...")
    log.info("Destination: " + indexDir.getDirectory)

    val iw = new IndexWriter(indexDir, new IndexWriterConfig(Meta.luceneVersion, analyzer))
    io.Source.fromFile(file).getLines().
      foreach {
      line => clog.execute {
        val splitted = line.trim.split("\t")
        addToIndex(iw, (splitted(0), splitted(1)))
      }
    }
    iw.commit()
    iw.close()
    log.info("Done.")
  }
}

object Index {

  //  val analyzer: Analyzer = new RussianAnalyzer(Meta.luceneVersion)
  //  val config = new IndexWriterConfig(Meta.luceneVersion, analyzer)
  //
  //  println(config)
  def main(args: Array[String]) {
    new Index()
  }
}
