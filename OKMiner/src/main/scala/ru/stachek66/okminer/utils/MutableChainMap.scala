package ru.stachek66.okminer.utils


/**
 * Map for multiple values
 * @author alexeyev
 */
class MutableChainMap[A, B] {

  import collection._

  private val map = mutable.Map[A, List[B]]() withDefaultValue List.empty[B]

  def put(key: A, value: B) {
    map.put(key, value :: map(key))
  }

  def put(key: A, values: Iterable[B]) {
    map.put(key, map(key) ++ values)
  }

  def get(key: A): Iterable[B] = map(key)

  def iterator = map.iterator
}
