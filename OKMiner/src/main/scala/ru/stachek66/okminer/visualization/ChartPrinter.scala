package ru.stachek66.okminer.visualization

import java.awt.Color
import java.io.File
import org.jfree.chart.ChartUtilities
import org.jfree.chart.renderer.xy.{DefaultXYItemRenderer, XYItemRenderer}
import org.slf4j.LoggerFactory
import org.jfree.chart.axis.{NumberTickUnit, NumberAxis}

/**
 * A tool setting how to draw the plot and printing it to file
 * @author alexeyev
 */
object ChartPrinter {

  private val log = LoggerFactory.getLogger("image-printer")

  /**
   * Given a chart and a file, does the drawing into PNG file
   */
  def print(chart: ChartGenerator.Chart, file: File) {

    val renderer: XYItemRenderer = new DefaultXYItemRenderer()
    chart.getChart.getXYPlot.setRenderer(renderer)
    chart.getChart.getXYPlot.setBackgroundPaint(Color.white)
    chart.getChart.getXYPlot.setRangeGridlinePaint(Color.gray)

    chart.getChart.getXYPlot.getDomainAxis.setAutoRangeMinimumSize(3)
    chart.getChart.getXYPlot.getDomainAxis.asInstanceOf[NumberAxis].setTickUnit(new NumberTickUnit(1))

    try {
      ChartUtilities.saveChartAsPNG(file, chart.getChart, 1000, 500)
      log.info(s"Image for trend [${chart.getChart.getTitle.getText}] printed successfully.")
    }
    catch {
      case e: Exception => {
        log.error("Problems while printing image " + chart.getChart.getTitle)
      }
    }
  }

}
