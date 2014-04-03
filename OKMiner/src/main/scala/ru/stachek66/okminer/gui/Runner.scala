package ru.stachek66.okminer.gui

import org.slf4j.LoggerFactory
import swing._
import scala.swing.event.ButtonClicked
import util.Success
import util.Failure
import utils.LogTextArea
import ru.stachek66.okminer.Meta.singleContext
import java.awt.Desktop
import scala.IllegalArgumentException
import java.io.IOException

/**
 * The GUI entry point of the application.
 * @author alexeyev
 */
object Runner extends SimpleSwingApplication {

  private val log = LoggerFactory.getLogger("gui-logger")

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

    // call for starting reports building
    val reportsButton = new Button {
      this.text = "Build!"
    }

    // call for starting graphs drawing
    val graphsButton = new Button {
      this.text = "Draw!"
    }

    // call for opening the chosen destination folder
    val openDestButton = new Button {
      this.text = "Open destination"
    }

    // text area used for logging
    val logsArea = LogTextArea

    // a scrollable container for text
    val scrollPane = new ScrollPane(
      new BorderPanel {
        add(logsArea, BorderPanel.Position.Center)
      }) {
      border = Swing.LineBorder(java.awt.Color.BLACK)
    }

    val defaultPosition = BorderPanel.Position.Center

    // filling the frame with components and setting layout
    contents = new GridPanel(1, 2) {

      val buttonsPanel = new BoxPanel(Orientation.Vertical) {

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
        contents += new BorderPanel {
          add(openDestButton, defaultPosition)
        }
        border = Swing.EmptyBorder(20, 20, 20, 20)
      }

      contents += buttonsPanel
      contents += scrollPane
    }

    // adding listeners
    listenTo(corpusDirectory.button)
    listenTo(destDirectory.button)
    listenTo(reportsButton)
    listenTo(graphsButton)
    listenTo(openDestButton)

    // reactive behaviour setting
    reactions += {
      // "setting corpus" action
      case ButtonClicked(button)
        if button.equals(corpusDirectory.button) => corpusDirectory.actOnClick()

      // "choosing destination" action
      case ButtonClicked(button)
        if button.equals(destDirectory.button) => destDirectory.actOnClick()

      // "building reports" action
      case ButtonClicked(button) if button.equals(reportsButton) =>
        concurrent.future {
          button.enabled = false
          DataProcessingTasks.buildReports(corpusDirectory.getFile, destDirectory.getFile)
          button.enabled = true
        } onComplete {
          case Success(u) =>
            Dialog.showMessage(button, "Reports building done!", "Message", Dialog.Message.Info)
          case Failure(e) =>
            Dialog.showMessage(
              button, "Something went wrong while building reports: " + e.getMessage, "Error", Dialog.Message.Error)
            log.info("Please fix me", e)
            button.enabled = true
        }

      // "graphs drawing" action
      case ButtonClicked(button) if button.equals(graphsButton) =>
        concurrent.future {
          button.enabled = false
          DataProcessingTasks.drawGraphs(destDirectory.getFile)
          button.enabled = true
        } onComplete {
          case Failure(e) =>
            Dialog.showMessage(button, "Something went wrong while drawing graphs: " + e.getMessage, "Error", Dialog.Message.Error)
            log.debug("Please fix me", e)
            button.enabled = true
          case Success(_) =>
            Dialog.showMessage(button, "Graphs flushing done!", "Message", Dialog.Message.Info)
        }

      // "open destination" action
      case ButtonClicked(button) if button.equals(openDestButton) =>
        try {
          Desktop.getDesktop.open(destDirectory.getFile.getOrElse(throw new IllegalArgumentException))
        } catch {
          case e: IOException =>
            Dialog.showMessage(
              button,
              "This option is not available for your OS, please open the folder manually",
              "Sorry", Dialog.Message.Info)
          case e: IllegalArgumentException =>
            Dialog.showMessage(button, "Please set destination directory", "Message", Dialog.Message.Info)
          case e: Exception =>
            Dialog.showMessage(button, "An error occurred ", "Message", Dialog.Message.Error)
            log.error("meh", e)
        }
    }
  }
}
