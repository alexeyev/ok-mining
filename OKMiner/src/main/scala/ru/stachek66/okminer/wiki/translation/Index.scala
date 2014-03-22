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

object IndexProperties {
  type RuEn = (String, String)
  val (ru, en) = ("ru", "en")
}

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

/**
 * Wikipedia-based translation.
 * @author alexeyev
 */
class Searcher(index: Index) {

  import IndexProperties._

  val reader = IndexReader.open(index.index)
  val searcher = new IndexSearcher(reader)
  val qp = new QueryParser(Meta.luceneVersion, ru, index.analyzer)

  def search(freeTextQuery: String, threshold: Option[Float] = None): Iterable[(Float, RuEn)] = {
    //todo: add score threshold
    val collector = TopScoreDocCollector.create(10000, true)
    searcher.search(qp.parse(freeTextQuery), collector)
    for {
      scoreDoc <- collector.topDocs().scoreDocs.toIterable
      if threshold.filter(_ > scoreDoc.score).isEmpty
      doc = searcher.doc(scoreDoc.doc)
    } yield (scoreDoc.score, (doc.getField(ru).stringValue(), doc.getField(en).stringValue()))
  }
}

object Tool {//extends App {

  val index = new Index(new File("parsed/ru-en-sorted.tsv"), new File("indices/lang_index"))
  val s = new Searcher(index)
  s.search("интернет вещей").foreach(println(_))

  def translate(text: String): Option[(String, String)] = s.search(text).headOption.map(_._2)


}
