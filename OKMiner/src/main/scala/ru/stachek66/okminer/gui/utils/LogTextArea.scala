package ru.stachek66.okminer.gui.utils

import swing.TextArea
import java.nio.charset.Charset
import java.io.{PrintStream, ByteArrayOutputStream}

/**
 * @author alexeyev
 */
object LogTextArea extends TextArea {

  text = "Waiting for logs..."

  editable = false

  private[utils] val stream = new PrintStream(
    new ByteArrayOutputStream() {
      override def write(b: Array[Byte], off: Int, len: Int) {
        text += new String(b.slice(off, off + len), Charset.forName("UTF-8"))
      }
    }
  )

  //  System.setOut(new PrintStream(stream))
  //  System.setErr(new PrintStream(stream))
  //  scala.Console.setOut(new PrintStream(stream))
  //  scala.Console.setErr(new PrintStream(stream))
}
