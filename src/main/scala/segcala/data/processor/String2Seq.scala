package segcala.data.processor

import segcala.data.{TextFragWithSeq, TextFrag, TextData}
import com.typesafe.scalalogging.slf4j.Logging
import scala.collection.mutable.ListBuffer
import segcala.util.{CharType, Chars}

/**
 * Date: 13-10-2
 * Time: 上午8:35
 * @author zhangyf
 */
class String2Seq(val isFilterEnglish: Boolean = true, override val next: Option[DataProcessor]) extends DataProcessor with Logging {

  def process(textData: TextData): Option[TextData] = {
    textData match {
      case TextFrag(origion) => {
        val seqData = isFilterEnglish match {
          case true => genSeq(origion)
          case false => {
            val result = Array.ofDim[String](2, origion.length)
            for (i <- 0 until origion.length) {
              result(0)(i) = origion.substring(i, i + 1)
              result(1)(i) = Chars.getStringType(result(0)(i)).toString
            }
            result
          }
        }
        val frag = TextFragWithSeq(origion, seqData)
        next match {
          case Some(processor) => processor.process(frag)
          case None => {
            logger.info("this is last processor: String2Seq")
            Some(frag)
          }
        }
      }
    }
  }

  def genSeq(str: String): Array[Array[String]] = {
    val charTypes = Chars.getType(str)
    val words = ListBuffer[String]()
    val types = ListBuffer[String]()
    val cur = new StringBuilder
    var prevType = CharType.C
    for (i <- 0 until str.length) {
      charTypes(i) match {
        case CharType.L => {
          if (prevType != CharType.L && i > 0) {
            words.append(cur.toString())
            types.append(Chars.charToStringType(charTypes(i - 1)).toString)
            prevType = CharType.L
            cur.clear()
          }
          cur.append(str.charAt(i))
        }
        case CharType.D => {
          if (prevType != CharType.D && i > 0) {
            words.append(cur.toString())
            types.append(Chars.charToStringType(charTypes(i - 1)).toString)
            prevType = CharType.D
            cur.clear()
          }
          cur.append(str.charAt(i))
        }
        case _ => {
          if (i > 0) {
            words.append(cur.toString())
            types.append(Chars.charToStringType(charTypes(i - 1)).toString)
            prevType = charTypes(i)
            cur.clear()
          }
          cur.append(str.charAt(i))
        }
      }
    }
    if (!cur.isEmpty) {
      words.append(cur.toString())
      types.append(Chars.charToStringType(charTypes(str.length - 1)).toString)
      cur.clear()
    }
    val data = new Array[Array[String]](2)
    data(0) = words.toArray
    data(1) = types.toArray
    data
  }

}
