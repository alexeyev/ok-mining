package ru.stachek66.okminer.wiki

import org.apache.lucene.index.IndexReader
import org.slf4j.LoggerFactory
import scala.util.{Success, Failure, Try}
import java.io.Closeable

/**
 * Abstraction of an object providing access to index.
 * @author alexeyev
 */
trait IndexHolder extends Closeable {

  private val log = LoggerFactory.getLogger(this.getClass)

  protected val indexDir: org.apache.lucene.store.Directory

  protected def doIndex(): Unit

  /**
   * Safe index directory getting
   */
  def accessIndex: org.apache.lucene.store.Directory = Try {
    IndexReader.open(indexDir)
  } match {
    case Failure(_) => {
      log.error("Index not found.")
      doIndex()
      indexDir
    }
    case Success(_) => {
      log.info("Index found.")
      indexDir
    }
  }

  override def close() {
    indexDir.close()
  }

}
