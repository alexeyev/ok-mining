package ru.stachek66.okminer.gui

import java.io.File
import javax.swing.JFileChooser
import scala.swing.FileChooser._
import scala.swing.{Dialog, FileChooser, Label, Button}

/**
 * Class for assistance with building GUI.
 * Implements common patterns of choosing a directory.
 * @author alexeyev
 */
private[gui] case class ImportantDirectory(button: Button,
                                           label: Label,
                                           chooser: FileChooser = new FileChooser(new File("../")) {
                                             this.fileSelectionMode = SelectionMode(JFileChooser.DIRECTORIES_ONLY)
                                           }) {
  /**
   * File to be chosen
   */
  private var file: File = null

  /**
   * Initial words to be shown as a label
   */
  private val initialText = label.text

  /**
   * @return a chosen file, if one was selected
   */
  def getFile: Option[File] = Option(file)

  /**
   * Reaction on the corresponding button click
   */
  def actOnClick() {
    label.text = "Opening..."
    val result: FileChooser.Result.Value = chooser.showOpenDialog(label)
    if (result.equals(FileChooser.Result.Approve)) {
      file = chooser.selectedFile
      label.text = "Directory chosen: " + file.getAbsolutePath
    } else {
      Dialog.showMessage(label, "No file chosen", "Message", Dialog.Message.Info)
      label.text = initialText
      file = null
    }
  }

  /**
   * @return a list of corresponding components to be shown
   */
  def getComponents = List(label, button)

}
