package ru.stachek66.okminer.gui

import scala.swing._
import scala.swing.event.ButtonClicked
import scala.util._

/**
 * The GUI entry point of the application.
 * @author alexeyev
 */
object Runner extends SimpleSwingApplication {

  def top = new MainFrame() {

    title = "GUI for tech references mining"
    preferredSize = new Dimension(900, 500)

    // choosing data for reading
    val corpusDirectory = ImportantDirectory(
      new Button {
        this.text = "Set corpus"
      }, new Label {
        this.text = "Please choose corpus"
      }
    )

    // choosing place for writing reports
    val destDirectory = ImportantDirectory(
      new Button {
        this.text = "Set destination"
      }, new Label {
        this.text = "Please choose destination"
      }
    )

    val reportsButton = new Button {
      this.text = "Build reports"
    }

    val graphsButton = new Button {
      this.text = "Draw graphs"
    }

    contents = new BoxPanel(Orientation.Vertical) {
      contents ++= corpusDirectory.getComponents
      contents ++= destDirectory.getComponents
      contents += reportsButton
      contents += graphsButton
      border = Swing.EmptyBorder(20, 20, 20, 20)
    }

    // adding listeners
    listenTo(corpusDirectory.button)
    listenTo(destDirectory.button)
    listenTo(reportsButton)
    listenTo(graphsButton)

    // reactive behaviour setting
    reactions += {
      case ButtonClicked(button)
        if button.equals(corpusDirectory.button) => corpusDirectory.actOnClick()
      case ButtonClicked(button)
        if button.equals(destDirectory.button) => destDirectory.actOnClick()
      case ButtonClicked(button)
        if button.equals(reportsButton) => Try {
        DataProcessingTasks.buildReports(corpusDirectory.getFile, destDirectory.getFile)
      } match {
        case Success(_) =>
          Dialog.showMessage(this.bounds, "Reports building done!", "Message", Dialog.Message.Info)
        case Failure(e) =>
          Dialog.showMessage(this.bounds, "Something went wrong while building reports: " + e.getMessage, "Error", Dialog.Message.Error)
      }
      case ButtonClicked(button)
        if button.equals(reportsButton) => Try {
        DataProcessingTasks.drawGraphs(corpusDirectory.getFile, destDirectory.getFile)
      } match {
        case Success(_) =>
          Dialog.showMessage(this.bounds, "Graphs flushing done!", "Message", Dialog.Message.Info)
        case Failure(e) =>
          Dialog.showMessage(this.bounds, "Something went wrong while drawing graphs: " + e.getMessage, "Error", Dialog.Message.Error)
      }
    }
  }
}