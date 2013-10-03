package segcala.data.processor

import com.typesafe.scalalogging.slf4j.Logging
import segcala.data.{TextFragWithSeqWithDataWithDic, TextFragWithSeqWithData, TextData}
import segcala.dictionary.Dictionary
import segcala.model.alphabet.LabelAlphabet

/**
 * Date: 13-10-2
 * Time: 下午8:20
 * @author zhangyf
 */
class GenDictData(val dict: Dictionary, val labels: LabelAlphabet, val next: Option[DataProcessor]) extends DataProcessor with Logging {

  val multiple = dict.isAmbiguity
  val idxB = labels.lookupIndex("B")
  val idxM = labels.lookupIndex("M")
  val idxE = labels.lookupIndex("E")
  val idxS = labels.lookupIndex("S")

  def process(textData: TextData): Option[TextData] = {
    textData match {
      case TextFragWithSeqWithData(origion, source, data) => {
        val length = source(0).length
        val result = Array.ofDim[Int](length, labels.size)
        val idxLen = dict.indexLen

        def doLabel(idx: Array[Int], i: Int) {
          for (k <- 0 until idx.length) {
            val n = idx(k)
            val s = getNextN(source(0), i, n)
            if (dict.contains(s.word)) {
              label(result, i, s.length)
              if (!multiple) {
                return
              }
            }
          }
        }

        for (i <- 0 until length) {
          if (i + idxLen <= length) {
            val wordInfo = getNextN(source(0), i, idxLen)
            val idx = dict.getIndex(wordInfo.word)
            if (idx != null) {
              doLabel(idx, i)
            }
          }
        }

        for (i <- 0 until length) {
          if (result(i).exists(_ == -1)) {
            for (j <- 0 until result(i).length) {
              result(i)(j) += 1
            }
          }
        }

        val textFragWithSeqWithDataWithDic = TextFragWithSeqWithDataWithDic(origion, source, data, result)

        next match {
          case Some(x) => x.process(textFragWithSeqWithDataWithDic)
          case None => {
            logger.info("this is the last processor: GenDicData")
            Some(textFragWithSeqWithDataWithDic)
          }
        }
      }
    }
  }

  private def label(tempData: Array[Array[Int]], i: Int, n: Int) {
    // 下面这部分依赖{1=B,2=M,3=E,0=S}
    if (n == 1) {
      tempData(i)(idxS) = -1
    } else {
      //首标记，-1表示做标记
      tempData(i)(idxB) = -1
      for (j <- i + 1 until i + n - 1) {
        //中间标记
        tempData(j)(idxM) = -1
        //尾标记
        tempData(i + n - 1)(idxE) = -1
      }
    }
  }

  /**
   * 得到从位置index开始的长度为N的字串
   */
  private def getNextN(data: Array[String], index: Int, n: Int): WordInfo = {
    val sb = new StringBuilder

    var i = index
    while (i < data.length && sb.length < n) {
      sb.append(data(i))
      i += 1
    }

    WordInfo(if (sb.length <= n) sb.toString() else sb.substring(0, n), i - index)

  }
}

case class WordInfo(word: String, length: Int)

