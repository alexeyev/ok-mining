package ru.stachek66.okminer.keywords

import ru.stachek66.okminer.corpus.CorpusStats


/**
 * @author alexeyev
 */
class KeywordsExtractor {


  def getTopK(text: String, formula: TfIdf) {
       //todo
  }


}

object Test {
  @deprecated
  def main(args: Array[String]) {
//    println(CorpusStats.docsInCorpus)
    println(CorpusStats.bags)
//    println(CorpusStats.docsInCorpus("скачаю"))
  }
}
