package ru.stachek66.okminer.visualization

/**
 * Some tuneable parameters can be set here
 * @author alexeyev
 */
case class DrawingConfig(
                          yearsAppropriate: Map[Int, Float] => Boolean = {
                            map => map.keySet.filter(year => map(year) > 0).size > 1
                          },
                          sufficientNumberOfYears: Map[Int, Float] => Boolean = {
                            map => map.keySet.filter(year => map(year) > 0).size > 1
                          })
