package segcala.mmseg

/**
 * 词
 * @param data 所属字符串
 * @param offset 偏移量
 * @param length  长度
 * @param frequency  词频
 */
class Word(val data: List[Char], val offset: Int, val length: Int, var frequency: Int) {
  def this(data: List[Char], offset: Int, length: Int) = this(data, offset, length, 0)

  def this(data: List[Char]) = this(data, 0, data.length, 0)

  def value: String = data.slice(offset, offset + length).mkString
}

/**
 *
 * @param words
 * @param lengthVal
 * @param averageLengthVal
 * @param varianceVal
 * @param degreeOfMorphemicFreedomVal
 */
class Chunk(val words: List[Word], var lengthVal: Int, var averageLengthVal: Double, var varianceVal: Double, var degreeOfMorphemicFreedomVal: Double) {
  def this(words: List[Word]) = this(words, -1, -1D, -1D, -1D)

  def averageLength: Double = {
    if (averageLengthVal < 0) {
      averageLengthVal = length.toDouble / words.length.toDouble
    }
    averageLengthVal
  }

  def variance: Double = {
    if (varianceVal < 0) {
      varianceVal = Math.sqrt(words.map(w => Math.pow(w.length.toDouble - averageLength, 2)).reduceLeft(_ + _))
    }
    varianceVal
  }

  //只有单字有词频
  def degreeOfMorphemicFreedom: Double = {
    if (degreeOfMorphemicFreedomVal < 0) {
      try {
        degreeOfMorphemicFreedomVal = words.filter(w => w.length == 1).map(w => Math.log(w.frequency.toDouble)).reduceLeft(_ + _)
      } catch {
        case e: Throwable => degreeOfMorphemicFreedomVal = 0
      }
    }
    degreeOfMorphemicFreedomVal
  }

  def length: Int = {
    if (lengthVal < 0) {
      lengthVal = words.map(w => w.length).reduceLeft(_ + _)
    }
    lengthVal
  }

  def count: Int = words.length
}

/**
 * 文本片断类
 * @param data
 * @param offset
 */
class TextFragment(val data: List[Char], var offset: Int) {
  def this(data: String) = this(data.toList, 0)

  def isFinish: Boolean = offset >= data.length
}
