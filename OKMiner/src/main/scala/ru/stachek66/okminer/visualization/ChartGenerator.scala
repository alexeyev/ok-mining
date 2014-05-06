package ru.stachek66.okminer.visualization

import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.{JFreeChart, ChartFactory}
import org.jfree.data.xy.{XYSeriesCollection, XYSeries}

/**
 * Given a data model, builds the corresponding jfree chart.
 * Points are scattered around the true points with the fixed coefficient.
 * @author alexeyev
 */
object ChartGenerator {

  /**
   * Coefficient setting the scattering.
   * Must be less than 1.0.
   */
  private val scattering = 0.2

  require(scattering < 1)

  class Chart(jFreeChart: JFreeChart) {
    private[visualization] def getChart = jFreeChart
  }

  /**
   * @param data model in concern
   * @return resulting chart
   */
  def buildChart(data: Model): Chart = {

    val dataset: XYSeriesCollection = new XYSeriesCollection

    for ((company: String, yearToScore) <- data.companyToYearToScore) {
      val series = new XYSeries(company)
      for ((year: Int, score: Float) <- yearToScore) {
        // scattering hack
        if (score != 0)
          series.add(year + (math.random - 0.5) * scattering, score + (math.random - 0.5) * scattering)
        else
          series.add(year, score)
      }
      dataset.addSeries(series)
    }

    new Chart(ChartFactory.createXYLineChart(
      data.trend, // title
      "years", // x
      "mentions", // y
      dataset,
      PlotOrientation.VERTICAL,
      true, true, false))
  }

}
