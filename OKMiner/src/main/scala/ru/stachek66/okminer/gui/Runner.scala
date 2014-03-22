package ru.stachek66.okminer.gui

import java.io.File
import javax.swing.JFileChooser
import scala.swing.FileChooser.SelectionMode
import scala.swing._
import scala.swing.event.ButtonClicked

/**
 * The GUI entry point of the application.
 * @author alexeyev
 */
object Runner extends SimpleSwingApplication {

  private var corpusFile: File = null
  private var destinationFile: File = null

  def top = new MainFrame() {
    title = "GUI for tech references mining"

    val corpusChooser = new FileChooser {
      this.fileSelectionMode = SelectionMode(JFileChooser.DIRECTORIES_ONLY)
    }
    val destinationChooser = new FileChooser {
      this.fileSelectionMode = SelectionMode(JFileChooser.DIRECTORIES_ONLY)
    }

    val corpusLabel = new Label {
      this.text = "Please choose corpus"
    }
    val corpusButton = new Button {
      this.text = "Set corpus"
    }

    val destinationLabel = new Label {
      this.text = "Please choose destination"
    }
    val destinationButton = new Button {
      this.text = "Set destination"
    }

    contents = new BoxPanel(Orientation.Vertical) {
      contents += corpusLabel
      contents += corpusButton
      contents += destinationLabel
      contents += destinationButton
      border = Swing.EmptyBorder(100, 100, 100, 100)
    }

    listenTo(corpusButton)
    listenTo(destinationButton)

    reactions += {
      case ButtonClicked(button) if button.equals(corpusButton) => {
        corpusLabel.text = "Opening corpus..."
        val result: FileChooser.Result.Value = corpusChooser.showOpenDialog(corpusLabel)
        if (result.equals(FileChooser.Result.Approve)) {
          corpusFile = corpusChooser.selectedFile
          corpusLabel.text = "Corpus directory chosen: " + corpusFile.getAbsolutePath
        } else {
          corpusLabel.text = "Please choose the corpus"
        }
      }
      case ButtonClicked(button) if button.equals(destinationButton) => {
        corpusLabel.text = "Opening destination..."
        val result: FileChooser.Result.Value = corpusChooser.showOpenDialog(destinationLabel)
        if (result.equals(FileChooser.Result.Approve)) {
          destinationFile = destinationChooser.selectedFile
          corpusLabel.text = "Destination directory chosen: " + destinationFile.getAbsolutePath
        } else {
          corpusLabel.text = "Please choose destination"
        }
      }
    }
  }
}
