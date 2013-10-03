package segcala.dictionary

import com.google.common.collect.{TreeMultimap, HashMultimap}
import scala.io.Source
import com.google.common.io.Resources
import com.google.common.base.Strings
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._
import java.util

/**
 * Date: 13-10-1
 * Time: 下午3:22
 * @author zhangyf
 */
object Dictionary {
  def apply(isAmbiguity: Boolean) = {
    new Dictionary("", isAmbiguity)
  }
}

class Dictionary(val path: String, val isAmbiguity: Boolean) {

  var minLen = Int.MaxValue
  var maxLen = Int.MinValue
  var indexLen = 2

  //词和对应的词性
  val dp: HashMultimap[String, String] = HashMultimap.create()
  val index: util.TreeMap[String, Array[Int]] = new util.TreeMap[String, Array[Int]]()

  if (!Strings.isNullOrEmpty(path)) {
    val loaded = loadDic(path)
    addToDic(loaded)
    createIndex()
  }

  def addSegDict(dicWords: List[String]) {
    addToDic(dicWords.filter(_.length > 0).map(_.trim.split("\\s")))
    createIndex()
  }

  def getIndex(s: String) = index.get(s)

  def getPOS(s: String) = dp.get(s)

  def contains(s: String) = dp.containsKey(s)


  def addToDic(loaded: List[Array[String]]) {
    loaded.foreach(arr => {
      val word = arr(0)
      maxLen = math.max(maxLen, word.length)
      minLen = math.min(minLen, word.length)
      //添加词性
      if (arr.length > 1) {
        arr.slice(1, arr.length).foreach(pos => {
          dp.put(word, pos)
        })
      }else{
        dp.put(word, null)
      }
    })
    indexLen = minLen
  }

  def createIndex() {
    indexLen = minLen
    val indexT: TreeMultimap[String, Integer] = TreeMultimap.create()
    dp.keySet().asScala.foreach(word => {
      if (word.length >= indexLen) {
        val temp = word.substring(0, indexLen)
        indexT.put(temp, word.length)
      }
    })
    indexT.asMap().asScala.foreach {
      case (s, c) => {
        index.put(s, c.asScala.toArray.reverse.map(_.toInt))
      }
    }
  }

  def loadDic(path: String): List[Array[String]] = {
    val lb = ListBuffer[Array[String]]()
    val lines = Source.fromInputStream(Resources.getResource(path).openStream(), "UTF-8").getLines()
    for (line <- lines) {
      val trimed = Strings.nullToEmpty(line).trim
      if (trimed.length > 0) {
        lb.append(trimed.split("\\s"))
      }
    }
    lb.toList
  }

}
