package ru.stachek66.okminer

import org.apache.lucene.util.Version
import java.util.concurrent.{TimeUnit, ForkJoinPool, Executors}
import concurrent.ExecutionContext
import org.slf4j.LoggerFactory
import scala.concurrent.duration.Duration

/**
 * Global immutable variables for referencing, if necessary.
 * @author alexeyev
 */
object Meta {

  private val log = LoggerFactory.getLogger("main")

  val luceneVersion = Version.LUCENE_46

  //  lazy val executorService = {
  //    val numberOfThreads = 4
  //    log.info(s"Using ${numberOfThreads} threads")
  //    ExecutionContext.Implicits.global
  //  }
  implicit lazy val singleContext = ExecutionContext.Implicits.global //ExecutionContext.fromExecutorService(executorService)

  val maxDuration = Duration(48, TimeUnit.HOURS)

}
