package ru.stachek66.okminer.categories

/**
 * Normalizing English wiki-articles' names
 * @author alexeyev
 */
object Utils {

  /**
   * @param name wiki-article name
   * @return normalized name
   */
  def norm(name: String) = {
    name.replaceAll("Category:", "").replaceAll("Stub:", "").toLowerCase.trim
  }
}
