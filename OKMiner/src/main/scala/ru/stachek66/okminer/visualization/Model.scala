package ru.stachek66.okminer.visualization


/**
 * A model for drawing a single trend-oriented graph.
 * @author alexeyev
 */
case class Model(trend: String, companyToYearToScore: Map[String, Map[Int, Float]])