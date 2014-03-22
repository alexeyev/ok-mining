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

  def top = new MainFrame() {

    title = "GUI for tech references mining"

    val corpusDirectory = ImportantDirectory(
      new Button {
        this.text = "Set corpus"
      }, new Label {
        this.text = "Please choose corpus"
      }
    )

    val destDirectory = ImportantDirectory(
      new Button {
        this.text = "Set destination"
      }, new Label {
        this.text = "Please choose destination"
      }
    )

    contents = new BoxPanel(Orientation.Vertical) {
      contents ++= corpusDirectory.getComponents
      contents ++= destDirectory.getComponents
      border = Swing.EmptyBorder(10, 10, 10, 10)
    }

    listenTo(corpusDirectory.button)
    listenTo(destDirectory.button)

    reactions += {
      case ButtonClicked(button)
        if button.equals(corpusDirectory.button) => corpusDirectory.actOnClick()
      case ButtonClicked(button)
        if button.equals(destDirectory) => destDirectory.actOnClick()
    }
  }
}

case class ImportantDirectory(button: Button,
                              label: Label,
                              chooser: FileChooser = new FileChooser {
                                this.fileSelectionMode = SelectionMode(JFileChooser.DIRECTORIES_ONLY)
                              }) {
  private var file = null

  private val initialText = label.text

  def getFile: Option[File] = Option(file)

  def actOnClick() {
    label.text = "Opening..."
    val result: FileChooser.Result.Value = chooser.showOpenDialog(label)
    if (result.equals(FileChooser.Result.Approve)) {
      file = chooser.selectedFile
      label.text = "Destination directory chosen: " + file.getAbsolutePath
    } else {
      label.text = initialText
    }
  }

  def getComponents = List(label, button)

}