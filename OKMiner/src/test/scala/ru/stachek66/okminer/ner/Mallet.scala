package ru.stachek66.okminer.ner

import java.io._
import cc.mallet.fst.CRF
import java.util.regex.Pattern
import cc.mallet.pipe._
import iterator.FileIterator
import java.util
import cc.mallet.types.InstanceList
import scala.collection.JavaConversions._

/**
 * Mallet NER experiment
 * @author alexeyev
 */
object MalletBenchmark extends App {

  val ois = new ObjectInputStream(new FileInputStream(new File("model2")))
  val crf = ois.readObject().asInstanceOf[CRF]
  ois.close()

  val pipeList = new util.ArrayList[Pipe]()
  pipeList.add(new Input2CharSequence("UTF-8"))
  pipeList.add(new CharSequence2TokenSequence(Pattern.compile("[\\p{LD}]+([\\.-][\\p{LD}]+)*")))

  val serPipes = new SerialPipes(pipeList)

  val instanceList = new InstanceList(serPipes)

  val texts = new File("../corpus-test2/clean/2014")
  println(texts.listFiles().toList)

  val iter = new FileIterator(texts, Pattern.compile("(.*)"))
  instanceList.addThruPipe(iter)

  instanceList.iterator().foreach {
    instance => {
      println(instance.getData)
      instance.unLock()
      val labeled = crf.transduce(instance)
      println(labeled.getData)
      //        label
    }
  }
}
