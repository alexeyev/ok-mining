package ru.stachek66.okminer.keywords

import ru.stachek66.okminer.corpus.CorpusStats

/**
 * @author alexeyev
 */
class BasicTfIdf extends TfIdf {

  import CorpusStats._

  def eval(term: String, termCount: Int, maxTermCount: Int): Double = {
    (termCount.toDouble / (maxTermCount.toDouble + 1)) * // tf
      math.log(docsInCorpus.toDouble / (docsInCorpus(term).toDouble + 1)) // idf
  }
}
