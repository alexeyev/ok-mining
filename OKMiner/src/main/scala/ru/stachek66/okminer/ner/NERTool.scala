package ru.stachek66.okminer.ner

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.{Field, TextField, Document}
import org.apache.lucene.index._
import org.apache.lucene.sandbox.queries.SlowFuzzyQuery
import org.apache.lucene.search.{TopScoreDocCollector, IndexSearcher}
import org.apache.lucene.store.RAMDirectory
import ru.stachek66.okminer.Meta

/**
 * Experiment
 * @author alexeyev
 */
object NERTool extends App {

  private def addToIndex(iw: IndexWriter, company: String) {
    val doc = new Document()
    doc.add(new TextField("name", company, Field.Store.YES))
    doc.add(new TextField("name", company, Field.Store.YES))
    iw.addDocument(doc)
  }

  val analyzer: Analyzer = new StandardAnalyzer(Meta.luceneVersion)
  val index = new RAMDirectory()
  val config = new IndexWriterConfig(Meta.luceneVersion, analyzer)
  val iw = new IndexWriter(index, config)
  addToIndex(iw, "Google")
  addToIndex(iw, "HP")
  addToIndex(iw, "Kle")
  addToIndex(iw, "IMB")
  addToIndex(iw, "KDIMB")
  addToIndex(iw, "Apple")
  iw.close()

  /*
   * Sadly, one can't use FuzzyQuery: max edit distance = 2
   * UNACCEPTABLE!
   *
   * CITING:
   * * @param minimumSimilarity a value between 0 and 1 to set the required similarity
   *  between the query term and the matching terms. For example, for a
   *  <code>minimumSimilarity</code> of <code>0.5</code> a term of the same length
   *  as the query term is considered similar to the query term if the edit distance
   *  between both terms is less than <code>length(term)*0.5</code>
   *  <p>
   *  Alternatively, if <code>minimumSimilarity</code> is >= 1f, it is interpreted
   *  as a pure Levenshtein edit distance. For example, a value of <code>2f</code>
   *  will match all terms within an edit distance of <code>2</code> from the
   *  query term. Edit distances specified in this way may not be fractional.
   */
  val fq = new SlowFuzzyQuery(new Term("name", "Shmoogle"), 4)

  //  val q = new QueryParser(Meta.luceneVersion, "name", analyzer).parse(freeTextQuery)

  val reader = IndexReader.open(index)
  val searcher = new IndexSearcher(reader)

  val collector = TopScoreDocCollector.create(10, true)
  searcher.search(fq, collector)
  val hits =
    collector.topDocs().
      scoreDocs.toIterable.
      map(
      document =>
        searcher.doc(document.doc))
  println(hits.toList)

}
