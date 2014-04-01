package ru.stachek66.okminer.categories

/**
 * @author alexeyev
 */
object Utils {

  def norm(name: String) = {
    name.replaceAll("Category:", "").replaceAll("Stub:", "").toLowerCase.trim
  }
}
