package ru.stachek66.okminer.gui.utils

import swing.TextArea
import java.nio.charset.Charset
import java.io.{PrintStream, ByteArrayOutputStream}
import javax.swing.text.DefaultCaret

/**
 * @author alexeyev
 */
object LogTextArea extends TextArea {

  text = "Waiting for logs...\n"

  editable = false

  {
    //Java-style solution
    val caret = peer.getCaret.asInstanceOf[DefaultCaret]
    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE)

  }

  @volatile
  private var logSize = 0
  private val maxLogSize = 400
  private val dec = 50
  private val pattern = ("([^\\n]+\\n){" + dec + "}")

  private[utils] val stream = new PrintStream(
    new ByteArrayOutputStream() {
      override def write(b: Array[Byte], off: Int, len: Int) {
        logSize += 1
        if (logSize >= maxLogSize) {
          text = text.replaceFirst(pattern, "")
          logSize -= dec
        }
        text += new String(b.slice(off, off + len), Charset.forName("UTF-8"))
      }
    }
  )
}
