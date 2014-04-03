package ru.stachek66.okminer.utils.storage

import org.slf4j.LoggerFactory

/**
 * Provides safe access to stored bag of triples:
 * (year, trend, organization)
 * @author alexeyev
 */
abstract class StoredBag {

  private val log = LoggerFactory.getLogger("stored-bag")

  protected def createDb(): Unit

  protected def dropDb(): Unit

  protected def getMyDao: Dao

  /**
   * The only access point to data storage.
   * Database is initially empty and after all the work is done
   * all data are removed.
   *
   * @param handler action to be done with stored bag
   */
  def withDao(handler: Dao => Unit) {
    try {
      dropDb()
      log.info("DB dropped successfully")
    } catch {
      case e: Exception => log.error("There was no DB")
    }
    createDb()
    handler(getMyDao)
    dropDb()
  }
}
