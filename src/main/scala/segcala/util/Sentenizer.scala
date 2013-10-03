package segcala.util

import com.google.common.base.{Strings, Preconditions}
import scala.collection.mutable.ListBuffer

/**
 * 简单的分割句子
 */
object Sentenizer {

  val seperators: List[Char] = List('，', '。', '？', '！')

  def split(sen: String): List[String] = {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(sen))
    val result = ListBuffer[String]()
    val current = new StringBuilder()
    for (i <- 0 until sen.length) {
      val c = sen.charAt(i)
      current.append(c)
      if (seperators.contains(c)) {
        result.append(current.toString())
        current.clear()
      }
    }
    if(current.length > 0){
      result.append(current.toString())
    }
    result.toList
  }

  def main(args: Array[String]) {
    println(split("a。b！以为？中国"))
  }

}
