package ru.stachek66.okminer

import java.io.File
import java.util
import ru.stachek66.okminer.visualization.{ChartPrinter, ChartGenerator, Model}

/**
 * @author alexeyev
 */
object GraphsExperiments extends App {

  val m = Model(
    "robotics",
    Map(
      "Google" -> Map(
        2002 -> 0,
        2003 -> 0,
        2004 -> 0,
        2005 -> 0,
        2006 -> 0,
        2007 -> 0,
        2010 -> 2,
        2012 -> 2,
        2013 -> 12
      ),
      "Amazon" -> Map(
        2002 -> 0,
        2003 -> 0,
        2004 -> 0,
        2005 -> 0,
        2006 -> 0,
        2007 -> 2,
        2010 -> 8,
        2012 -> 10,
        2013 -> 15
      )
    )
  )

  val (chart) = ChartGenerator.buildChart(m)
  ChartPrinter.print(chart, new File("../../Desktop/" + m.trend + new util.Date().getTime + ".png"))
}
