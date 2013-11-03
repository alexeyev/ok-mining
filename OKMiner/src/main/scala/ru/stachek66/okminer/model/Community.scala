package ru.stachek66.okminer.model

/**
 * @author alexeyev
 */
case class Community(
                      description: String,
                      posts: String,
                      links: Iterable[String],
                      linkedTexts: Option[Iterable[String]],
                      name: Option[String]
                      )
