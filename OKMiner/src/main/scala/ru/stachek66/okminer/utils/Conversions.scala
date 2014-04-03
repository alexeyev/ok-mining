package ru.stachek66.okminer.utils

import scala.collection.mutable.{Map => mMap}

/**
 * Unclassified utils for specific and general purposes.
 * @author alexeyev
 */
object Conversions {

  /**
   * Mutable map of mutable maps to immutable map of immutable maps.
   * Sadly, there is to recursive 'toMap' method.
   * @param mmap target mutable map
   * @return immutable map
   */
  def toImmutable[X, Y, Z](mmap: mMap[X, mMap[Y, Z]]): Map[X, Map[Y, Z]] = {
    for ((key, mmapValue) <- mmap)
    yield key -> mmapValue.toMap
  } toMap

}
