package ru.stachek66.okminer.utils

import org.slf4j.Logger

/**
 * Logging repeated action to show progress.
 * @param log user's logger
 * @param step number of ticks to skip before logging
 * @param pattern text for flushing to log with a given step
 *                Please use %s or %d interpolation of pattern!
 * @author alexeyev
 */
class CounterLogger(log: Logger, step: Int, pattern: String) {

  if (step < 1) throw new IllegalArgumentException("Step must be a positive integer.")

  def getLogger = log

  def execute[T](action: => T): T = {
    val result = action
    tick()
    result
  }

  def getCurrentCount = counter

  private def tick() {
    counter += 1
    if (counter % step == 0) {
      log.info(pattern.format(counter))
      prevCounter = counter
    }
  }

  private def tick(up: Int) {
    counter += up
    if (counter - prevCounter >= step) {
      log.info(pattern.format(counter))
      prevCounter = counter
    }
  }

  @volatile
  private var counter: Long = 0
  @volatile
  private var prevCounter: Long = 0
}

