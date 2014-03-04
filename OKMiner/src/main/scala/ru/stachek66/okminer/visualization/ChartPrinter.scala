package ru.stachek66.okminer.visualization

import java.awt.Color
import java.io.File
import org.jfree.chart.ChartUtilities
import org.jfree.chart.renderer.xy.{XYSplineRenderer, XYItemRenderer}
import org.slf4j.LoggerFactory

/**
 * @author alexeyev
 */
object ChartPrinter {

  private val log = LoggerFactory.getLogger("image-printer")

  def print(chart: ChartGenerator.Chart, file: File) {

    val renderer: XYItemRenderer = new XYSplineRenderer
    chart.getChart.getXYPlot.setRenderer(renderer)
    chart.getChart.getXYPlot.setBackgroundPaint(Color.white)
    chart.getChart.getXYPlot.setRangeGridlinePaint(Color.gray)

    try {
      ChartUtilities.saveChartAsPNG(file, chart.getChart, 900, 400)
      log.info("Image for trend [%s] printed successfully.".format(chart.getChart.getTitle.getText))
    }
    catch {
      case e: Exception => {
        log.error("Problems while printing image " + chart.getChart.getTitle)
      }
    }
  }

}
