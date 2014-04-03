package ru.stachek66.okminer.gui.utils

import ch.qos.logback.core.OutputStreamAppender

/**
 * A custom logback appender for writing logs right into the Swing window.
 */
class TextFieldAppender2 extends OutputStreamAppender[String] {

  override def start() {
    setOutputStream(LogTextArea.stream)
    super.start()
  }

}

