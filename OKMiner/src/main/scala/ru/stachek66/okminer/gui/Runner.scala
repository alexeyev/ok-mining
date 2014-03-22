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
    preferredSize = new Dimension(450, 500)

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
      this.text = "Build!"
    }

    val graphsButton = new Button {
      this.text = "Draw!"
    }

    val defaultPosition = BorderPanel.Position.Center

    contents = new BoxPanel(Orientation.Vertical) {
      contents ++=
        (corpusDirectory.getComponents ++ destDirectory.getComponents).map {
          case component =>
            new BorderPanel {
              add(component, defaultPosition)
            }
        }
      contents += new BorderPanel {
        add(new Label {
          this.text = "Reports:"
        }, defaultPosition)
      }
      contents += new BorderPanel {
        add(reportsButton, defaultPosition)
      }
      contents += new BorderPanel {
        add(new Label {
          this.text = "Graphs:"
        }, defaultPosition)
      }
      contents += new BorderPanel {
        add(graphsButton, defaultPosition)
      }
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

      case ButtonClicked(button) if button.equals(reportsButton) =>
        Try {
          DataProcessingTasks.buildReports(corpusDirectory.getFile, destDirectory.getFile)
        } match {
          case Success(_) =>
            Dialog.showMessage(
              button, "Reports building done!", "Message", Dialog.Message.Info)
          case Failure(e) =>
            Dialog.showMessage(
              button, "Something went wrong while building reports: " + e.getMessage, "Error", Dialog.Message.Error)
        }
      case ButtonClicked(button) if button.equals(graphsButton) =>
        Try {
          DataProcessingTasks.drawGraphs(destDirectory.getFile)
        } match {
          case Success(_) =>
            Dialog.showMessage(button, "Graphs flushing done!", "Message", Dialog.Message.Info)
          case Failure(e) =>
            Dialog.showMessage(button, "Something went wrong while drawing graphs: " + e.getMessage, "Error", Dialog.Message.Error)
        }
    }
  }
}