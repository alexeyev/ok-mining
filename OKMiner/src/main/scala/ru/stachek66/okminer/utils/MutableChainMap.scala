package ru.stachek66.okminer.utils


/**
 * Map for multiple values
 * @author alexeyev
 */
class MutableChainMap[A, B] {

  import collection._

  private val map = collection.mutable.Map[A, mutable.Set[B]]() withDefaultValue mutable.Set.empty[B]

  def put(key: A, value: B) {
    map.put(key, map(key) + value)
  }

  def put(key: A, values: Iterable[B]) {
    map.put(key, map(key) ++ values)
  }

  def get(key: A): Iterable[B] = map(key)
}
