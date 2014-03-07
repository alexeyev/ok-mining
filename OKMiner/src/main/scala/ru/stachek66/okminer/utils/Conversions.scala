package ru.stachek66.okminer.utils

import scala.collection.mutable.{Map => mMap}

/**
 * @author alexeyev
 */
object Conversions {

  def toImmutable(mmap: mMap[String, mMap[Int, Float]]): Map[String, Map[Int, Float]] = {
    for ((key, mmapValue) <- mmap)
    yield key -> mmapValue.toMap
  } toMap

}
