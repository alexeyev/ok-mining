package ru.stachek66.okminer

import org.slf4j.LoggerFactory
import utils.FileUtils
import org.json.JSONObject
import java.io.File

/**
 * DrawingConfig for different application parameters
 * @author alexeyev
 */
object JsonConfigReader {

  private val log = LoggerFactory.getLogger("json-config-reader")

  val config: Config = try {
    val j = new JSONObject(FileUtils.asStringWithoutNewLines(new File("config.json")))
    Config(useKeyPhrasenessThreshold = j.getBoolean("use-keyphraseness"))
  } catch {
    case e: Exception =>
      log.error("No config found, using default")
      Config()
  }

}

private[okminer] case class Config(useKeyPhrasenessThreshold: Boolean = false)