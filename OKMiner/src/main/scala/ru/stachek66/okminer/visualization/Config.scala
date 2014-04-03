package ru.stachek66.okminer.visualization

/**
 * Some tuneable parameters can be set here
 * @author alexeyev
 */
case class Config(
                   yearsAppropriate: Set[Int] => Boolean = years => years.size > 1,
                   sufficientNumberOfYears: Set[Int] => Boolean = years => years.size > 1
                   )
