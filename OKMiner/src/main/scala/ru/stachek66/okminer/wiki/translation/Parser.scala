package ru.stachek66.okminer.wiki.translation

import java.io.File
import scala.collection.mutable.ArrayBuffer

/**
 * @author alexeyev
 */
object Parser extends App {

  private val dump = new File("../extwiki-20131231-langlinks.sql")
  private val pattern = "[ ,]\\((\\d+,'%s','[^']+')\\)[,; ]"

  private val rup = pattern.format("ru").r
  private val enp = pattern.format("en").r

  private val map = collection.mutable.Map[Long, ArrayBuffer[String]]() withDefault (a => ArrayBuffer())

  io.Source.fromFile(dump).getLines().foreach {
    line => {
//      if (line.contains("INSERT")) {
        rup.findAllIn(line).
          matchData.foreach(
          m => {
            val spl = m.group(1).split(",")
            val id = spl(0).toLong
            val tal = spl.tail.tail.mkString(",")
            map.get(id) match {
              case Some(a) => map.put(id, a ++ ArrayBuffer(tal))
              case None => map.put(id, ArrayBuffer(tal))
            }
          })

        enp.findAllIn(line).
          matchData.foreach(
          m => {
            val spl = m.group(1).split(",")
            val id = spl(0).toLong
            val tal = spl.tail.tail.mkString(",")
            map.get(id) match {
              case Some(a) => map.put(id, a ++ ArrayBuffer(tal))
              case None => map.put(id, ArrayBuffer(tal))
            }
          })
//      }
//      println(map)
    }
  }

  map.foreach(println(_))
}
