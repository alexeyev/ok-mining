package ru.stachek66.okminer.utils.storage

import org.slf4j.LoggerFactory
import scala.Array
import redis.clients.jedis._
import scala.collection.JavaConversions._

/**
 * Redis usage attempt.
 * @author alexeyev
 */
object Storage2 {

  private val log = LoggerFactory.getLogger("redis")

  val pool = {
    val host = "127.0.0.1"
    val port = 6379
    val shard = List[JedisShardInfo](new JedisShardInfo(host, port))
    val config = new JedisPoolConfig()
    config.setMaxIdle(10)

    //        config.maxActive = 1000;
    //        config.maxIdle = 10;
    //        config.minIdle = 1;
    //        config.maxWait = 30000;
    //        config.numTestsPerEvictionRun = 3;
    //        config.testOnBorrow = true;
    //        config.testOnReturn =  true;
    //        config.testWhileIdle =  true;
    config.setTimeBetweenEvictionRunsMillis(30000) //timeBetweenEvictionRunsMillis = 30000;
    new ShardedJedisPool(config, shard)
  }

  private val redisResource = pool.getResource
  private val redis = redisResource.getShard("")

  private def createDb() {

  }

  private def dropDb() {

  }

  private object RedisDao extends Dao {

    def put(data: Iterable[(Int, String, String)]) {

      log.info(s"Batch size: ${data.size}")

      for {
        (year, trend, company) <- data
      } redis.incr(List(year.toString, trend, company).mkString("\t"))

    }

    def getStats(year: Int): Iterator[(String, String, Int)] = {

      //      new Iterator[(String, String, Int)]() {
      //
      //        def next() = {
      //          null
      //        }
      //
      //        def hasNext: Boolean = {
      //          false
      //        }
      //      }
      throw new NotImplementedError()
    }
  }


  def withDao(handler: Dao => Unit) {
    try {
      dropDb()
      log.info("DB dropped successfully")
    } catch {
      case e: Exception =>
        log.error("There was no DB")
    }
    createDb()
    handler(RedisDao)
    dropDb()
  }

  def main(args: Array[String]) {
    RedisDao.put(List((234, ",", "sdf")))
  }
}
