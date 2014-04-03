package ru.stachek66.okminer.utils.storage

/**
 * Providing access to data storage.
 * @author alexeyev
 */
abstract class Dao {

  /**
   * Incrementing counter in the 'stored bag' for a given triple.
   */
  def put(s: Iterable[(Int, String, String)]): Unit

  /**
   * Given a year, returns stats by trends and organizations
   * @param year target year
   * @return triples: (trend, company, mentions-in-provided-year)
   */
  def getStats(year: Int): Iterator[(String, String, Int)]
}

