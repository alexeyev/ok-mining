package ru.stachek66.okminer.wiki.translation

import java.io.File
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.apache.lucene.document.{Field, TextField, Document}
import org.apache.lucene.index.{IndexReader, IndexWriterConfig, IndexWriter}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{TopScoreDocCollector, IndexSearcher}
import org.apache.lucene.store.NIOFSDirectory
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.Meta
import scala.util.{Success, Failure, Try}


private[translation] class Index(mapFile: File, destDirectory: File) {

  import IndexProperties._

  private def addToIndex(iw: IndexWriter, pair: RuEn) {
    val doc = new Document()
    doc.add(new TextField(ru, pair._1, Field.Store.YES))
    doc.add(new TextField(en, pair._2, Field.Store.YES))
    iw.addDocument(doc)
  }

  val analyzer: Analyzer = new RussianAnalyzer(Meta.luceneVersion)
  val index = new NIOFSDirectory(destDirectory)

  private val config = new IndexWriterConfig(Meta.luceneVersion, analyzer)
  private val log = LoggerFactory.getLogger("lang-index")

  def fillIndex(file: File) {
    log.info("Filling lang index...")
    val iw = new IndexWriter(index, config)
    io.Source.fromFile(file).getLines().
      foreach {
      line => {
        val splitted = line.trim.split("\t")
        addToIndex(iw, (splitted(0), splitted(1)))
      }
    }
    iw.commit()
    iw.close()
    log.info("Done.")
  }

  Try {
    val checker = IndexReader.open(index)
  } match {
    case Failure(_) => fillIndex(mapFile)
    case Success(_) => log.info("Index found.")
  }
}
