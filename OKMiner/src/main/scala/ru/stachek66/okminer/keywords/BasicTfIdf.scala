package ru.stachek66.okminer.keywords

import ru.stachek66.okminer.corpus.CorpusStats

/**
 * @author alexeyev
 */
class BasicTfIdf(term: String, termCount: Int, maxTermCount: Int) extends TfIdf {

  import CorpusStats._

  def eval(): Double = {
    (termCount.toDouble / maxTermCount.toDouble) * // tf
      math.log(docsInCorpus.toDouble / docsInCorpus(term).toDouble) // idf
  }
}
