package ru.stachek66.okminer.gui

import javax.swing.JFileChooser
import scala.swing.FileChooser.SelectionMode
import scala.swing._
import scala.swing.event.ButtonClicked

/**
 * The GUI entry point of the application.
 * @author alexeyev
 */
object Runner {
  def main(args: Array[String]) {
    println("Hello world!")

  }
}

object FirstSwingApp extends SimpleSwingApplication {
  def top = new MainFrame() {
    title = "Графический интерфейс к средствам анализа представленности технологий в медиа"

    val corpusLabel = new Label {
      this.text = "Please choose the corpus"
    }

    val corpusChooser = new FileChooser {
      this.fileSelectionMode = SelectionMode(JFileChooser.DIRECTORIES_ONLY)
    }
    val corpusButton = new Button {
      this.text = "Set corpus"
    }

    contents = new BoxPanel(Orientation.Vertical) {
      contents += corpusLabel
      contents += corpusButton
      border = Swing.EmptyBorder(10, 10, 10, 30)
    }

    listenTo(corpusButton)

    reactions += {
      case ButtonClicked(button) if button.equals(corpusButton) =>
        corpusLabel.text = "Opening corpus..."
        val result: FileChooser.Result.Value = corpusChooser.showOpenDialog(corpusLabel)
        if (result.equals(FileChooser.Result.Approve))
          println(corpusChooser.selectedFile)
    }
  }
}
