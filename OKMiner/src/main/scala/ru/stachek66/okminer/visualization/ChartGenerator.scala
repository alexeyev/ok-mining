package ru.stachek66.okminer.visualization

import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.{JFreeChart, ChartFactory}
import org.jfree.data.xy.{XYSeriesCollection, XYSeries}

/**
 * @author alexeyev
 */
object ChartGenerator {

  class Chart(jFreeChart: JFreeChart) {
    private[visualization] def getChart = jFreeChart
  }

  def buildChart(data: Model): (String, Chart) = {

    val dataset: XYSeriesCollection = new XYSeriesCollection

    for ((company: String, yearToScore) <- data.companyToYearToScore) {
      val series = new XYSeries(company)
      for ((year: Int, score: Float) <- yearToScore) {
        series.add(year, score)
      }
      dataset.addSeries(series)
    }

    (data.trend,
      new Chart(ChartFactory.createXYLineChart(
        data.trend, // title
        "years", // x
        "mentions", // y
        dataset,
        PlotOrientation.VERTICAL,
        true, true, false)))
  }

}
