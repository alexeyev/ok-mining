package ru.stachek66.okminer.topics

import cc.mallet.pipe._
import cc.mallet.pipe.iterator._
import cc.mallet.topics._
import cc.mallet.types._
import java.io._
import java.util
import java.util._
import java.util.regex._

/**
 * Experiment: needs tuning
 * Adapted Mr McCallum's tutorial
 */
object TopicTool {

  private def buildPipeList: util.ArrayList[Pipe] = {
    val pipeList = new ArrayList[Pipe]()
    pipeList.add(new CharSequenceLowercase())
    pipeList.add(new StemmingPipe())
    pipeList.add(new TokenSequenceRemoveStopwords(
      new File("tools/clean_stopwords.txt"),
      "UTF-8",
      false, false, false))
    pipeList.add(
      new TokenSequence2FeatureSequence())
    pipeList
  }

  def main(args: Array[String]) {

    val source = new File("./test.txt")
    val instances = new InstanceList(new SerialPipes(buildPipeList))

    val fileReader = new InputStreamReader(new FileInputStream(source), "UTF-8")
    instances.addThruPipe(
      new CsvIterator(
        fileReader,
        Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
        3, 2, 1)
    ) // data, label, name fields

    // alpha_t = 0.01,
    // beta_w = 0.01
    //  Note that the first parameter is passed as the sum over topics, while
    //  the second is the parameter for a single dimension of the Dirichlet prior.
    val numTopics = 3

    /**
     * COPY-and-PASTE
     * Simple parallel threaded implementation of LDA,
     * following Newman, Asuncion, Smyth and Welling, Distributed Algorithms for Topic Models
     * JMLR (2009), with SparseLDA sampling scheme and data structure from
     * Yao, Mimno and McCallum, Efficient Methods for Topic Model Inference on Streaming Document Collections, KDD (2009).
     *
     * @author David Mimno, Andrew McCallum
     */
    val model = new ParallelTopicModel(numTopics, 1.0, 0.1)

    model.addInstances(instances)

    // Use two parallel samplers, which each look at one half the corpus and combine
    //  statistics after every iteration.
    model.setNumThreads(3)

    // Run the model for 50 iterations and stop (this is for testing only,
    //  for real applications, use 1000 to 2000 iterations)
    model.setNumIterations(200)
    model.estimate()

    // Show the words and topics in the first instance

    // The data alphabet maps word IDs to strings
    val dataAlphabet = instances.getDataAlphabet
    var out = new Formatter(Locale.US)

    // Estimate the topic distribution of the first instance,
    //  given the current Gibbs state.
    val topicDistribution = model.getTopicProbabilities(0)

    // Get an array of sorted sets of word ID/count pairs
    val topicSortedWords = model.getSortedWords

    // Show top 5 words in topics with proportions for the first document
    for (topic <- 0 until numTopics) {
      val iterator = topicSortedWords.get(topic).iterator()

      out = new Formatter(Locale.US)
      out.format("%d\t%.3f\t",
        topic.asInstanceOf[Object],
        topicDistribution(topic).asInstanceOf[Object])
      var rank = 0
      while (iterator.hasNext && rank < 15) {
        val idCountPair = iterator.next()
        out.format("%s (%.0f) ",
          dataAlphabet.lookupObject(idCountPair.getID).asInstanceOf[Object],
          idCountPair.getWeight.asInstanceOf[Object])
        rank += 1
      }
      System.out.println(out)
    }
  }

}
