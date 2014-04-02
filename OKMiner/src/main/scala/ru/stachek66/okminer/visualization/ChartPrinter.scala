package ru.stachek66.okminer.visualization

import java.awt.Color
import java.io.File
import org.jfree.chart.ChartUtilities
import org.jfree.chart.renderer.xy.{XYSplineRenderer, XYItemRenderer}
import org.slf4j.LoggerFactory
import org.jfree.chart.axis.{NumberTickUnit, NumberAxis}

/**
 * A tool setting how to draw the plot and printing it to file
 * @author alexeyev
 */
object ChartPrinter {

  private val log = LoggerFactory.getLogger("image-printer")

  def print(chart: ChartGenerator.Chart, file: File) {

    val renderer: XYItemRenderer = new XYSplineRenderer
    chart.getChart.getXYPlot.setRenderer(renderer)
    chart.getChart.getXYPlot.setBackgroundPaint(Color.white)
    chart.getChart.getXYPlot.setRangeGridlinePaint(Color.gray)

    chart.getChart.getXYPlot.getDomainAxis.setAutoRangeMinimumSize(3)
    chart.getChart.getXYPlot.getDomainAxis.asInstanceOf[NumberAxis].setTickUnit(new NumberTickUnit(1))

    try {
      ChartUtilities.saveChartAsPNG(file, chart.getChart, 1500, 800)
      log.info(s"Image for trend [${chart.getChart.getTitle.getText}] printed successfully.")
    }
    catch {
      case e: Exception => {
        log.error("Problems while printing image " + chart.getChart.getTitle)
      }
    }
  }

}
