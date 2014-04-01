package ru.stachek66.okminer.utils.storage

import org.apache.commons.dbcp.BasicDataSource
import org.springframework.jdbc.core.JdbcTemplate
import java.io.File
import java.sql.Types
import scala.collection.JavaConversions._
import org.slf4j.LoggerFactory

/**
 * @author alexeyev
 */
object Storage {

  private val log = LoggerFactory.getLogger("local-storage")

  private val dataSource = {
    val ds = new BasicDataSource()
    val storageFile = new File("parsed/statdb")
    ds.setUrl("jdbc:h2:" + storageFile.getAbsolutePath)
    ds.setPassword("")
    ds.setUsername("")
    ds
  }

  private val jdbc = new JdbcTemplate(dataSource)
  jdbc.setFetchSize(2000)

  private val table = "stats"

  private def createDb() {
    jdbc.execute(
      s"create table $table (id bigint auto_increment primary key, year int, trend varchar(400), company varchar(100))")
  }

  private def dropDb() {
    jdbc.execute(s"drop table $table")
  }

  private object H2Dao extends Dao {

    def put(data: Iterable[(Int, String, String)]) {

      log.debug(s"Batch size: ${data.size}")

      jdbc.batchUpdate(
        s"insert into $table values (?,?,?,?)",
        data.map {
          case (year, trend, company) =>
            Array(null, year.asInstanceOf[Object], trend.asInstanceOf[Object], company.asInstanceOf[Object])
        } toList,
        Array(Types.NUMERIC, Types.NUMERIC, Types.VARCHAR, Types.VARCHAR)
      )
    }

    def getStats(year: Int): Iterator[(String, String, Int)] = {

      val rowSet = jdbc.queryForRowSet(
        s"select year, company, trend, count(id) as num from $table " +
          s"where year = $year " +
          s"group by year, company, trend ")

      new Iterator[(String, String, Int)]() {

        def next() = {
          val company = rowSet.getString("company")
          val trend = rowSet.getString("trend")
          val num = rowSet.getInt("num")
          (trend, company, num)
        }

        def hasNext: Boolean = {
          rowSet.next()
        }
      }
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
    handler(H2Dao)
    dropDb()
  }


  def main(args: Array[String]) {
    withDao(d => println(d))
  }

}