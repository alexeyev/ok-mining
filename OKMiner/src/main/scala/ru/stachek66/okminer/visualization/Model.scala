package ru.stachek66.okminer.visualization


/**
 * A model for drawing a single trend-oriented graph.
 * @author alexeyev
 */
case class Model(trend: String, companyToYearToScore: scala.collection.Map[String, scala.collection.Map[Int, Float]])