package ru.stachek66.okminer.visualization

/**
 * @author alexeyev
 */
case class Model(trend: String, companyToYearToScore: Map[String, Map[Int, Float]])