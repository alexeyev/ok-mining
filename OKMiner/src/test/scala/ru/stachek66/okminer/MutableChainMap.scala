package ru.stachek66.okminer

import java.util
import scala.collection.JavaConversions._

/**
 * @author alexeyev
 */
class MutableChainMap[A, B] {

  private val map = collection.mutable.Map[A, List[B]]() withDefaultValue List.empty[B]

  def put(key: A, value: B) {
    map.put(key, value :: map(key))
  }

  def put(key: A, values: Iterable[B]) {
    map.put(key, map(key) ++ values)
  }

  def get(key: A): List[B] = map(key)
}
