package ru.stachek66.okminer.gui.utils

import swing.TextArea
import java.nio.charset.Charset
import java.io.{PrintStream, ByteArrayOutputStream}
import javax.swing.text.DefaultCaret

/**
 * A single Text Area element inheritor, providing access
 * to text as if it is a limited Stream. Used for logging.
 * @author alexeyev
 */
object LogTextArea extends TextArea {

  text = "Waiting for logs...\n"

  editable = false

  {
    //Java-style solution of the scrolling-down problem
    val caret = peer.getCaret.asInstanceOf[DefaultCaret]
    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE)

  }

  @volatile
  private var logSize = 0

  /**
   * Number-of-lines-decreasing step.
   */
  private val dec = 50

  /**
   * Maximum allowable number of lines to show.
   * Must be constrained due to the danger of
   * running out of memory.
   */
  private val maxLogSize = 400

  private val pattern = "([^\\n]+\\n){" + dec + "}"

  /**
   * TextArea as a stream
   */
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
