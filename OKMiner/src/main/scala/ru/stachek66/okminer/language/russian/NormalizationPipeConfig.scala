package ru.stachek66.okminer.language.russian

/**
 * @author alexeyev
 */
object NormalizationPipeConfig {

  def pipe(text: String): Iterable[String] =
    Stemmer.stem(
      StopWordsFilter.filter(
        Tokenizer.tokenize(text)))
}
