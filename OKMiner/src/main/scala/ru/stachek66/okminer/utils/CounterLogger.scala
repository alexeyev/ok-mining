package ru.stachek66.okminer.utils

import org.slf4j.Logger

/**
 * @author alexeyev
 */
class CounterLogger(log: Logger, step: Int, pattern: String) {
  //todo: write in scala-style, without ticks
  if (step < 1) throw new IllegalArgumentException("Step must be a positive integer.")

  def tick() {
    counter += 1
    if (counter % step == 0) {
      log.info(pattern.format(counter))
      prevCounter = counter
    }
  }

  def tick(up: Int) {
    counter += up
    if (counter - prevCounter >= step) {
      log.info(pattern.format(counter))
      prevCounter = counter
    }
  }

  def getCounter = counter

  @volatile
  private var counter: Long = 0
  @volatile
  private var prevCounter: Long = 0
}

