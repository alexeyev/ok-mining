package ru.stachek66.okminer.topics

import cc.mallet.pipe.Pipe
import cc.mallet.types.{FeatureSequenceWithBigrams, Token, TokenSequence, Instance}
import org.slf4j.LoggerFactory
import ru.stachek66.okminer.language.russian.Tokenizer
import scala.collection.JavaConversions._

/**
 * @author alexeyev
 */
class StemmingPipe extends Pipe {

  private val log = LoggerFactory.getLogger(this.getClass)

  override def pipe(carrier: Instance): Instance = {
    log.debug(">>> " + carrier.getData.asInstanceOf[CharSequence])
    val cs = carrier.getData.asInstanceOf[CharSequence].toString
    val ts = new TokenSequence(Tokenizer.tokenize(cs).map(new Token(_)))
    log.debug("<<< " + ts.toString)
    carrier.setData(ts)
    carrier
  }

}
