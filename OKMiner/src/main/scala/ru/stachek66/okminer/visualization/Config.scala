package ru.stachek66.okminer.visualization

/**
 * @author alexeyev
 */
case class Config(
                   yearsAppropriate: Set[Int] => Boolean =
                   years => years.size > 1
                   )
