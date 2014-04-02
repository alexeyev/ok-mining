package ru.stachek66.okminer.utils.storage

/**
 * @author alexeyev
 */
abstract class Dao {

  /**
   * Incrementing counter in the 'stored bag' for a given triple.
   */
  def put(s: Iterable[(Int, String, String)]): Unit

  def getStats(year: Int): Iterator[(String, String, Int)]
}

