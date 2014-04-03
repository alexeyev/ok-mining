package ru.stachek66.okminer.wiki.fetchers

trait Fetcher[T] {
  def fetch(handler: T => Unit): Unit
}
