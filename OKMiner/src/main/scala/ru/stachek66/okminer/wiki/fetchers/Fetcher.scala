package ru.stachek66.okminer.wiki.fetchers

/**
 * @author alexeyev
 */
trait Fetcher[T] {
  def fetch(handler: T => Unit): Unit
}
