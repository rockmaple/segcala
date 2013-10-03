package segcala.classifier

import scala.reflect.ClassTag


/**
 * Date: 13-9-21
 * Time: 下午4:51
 * @author zhangyf
 */
class Predict[T:ClassTag](val n: Int = 1) {

  val labels: Array[T] = new Array[T](n)
  val scores: Array[Float] = Array.fill[Float](n)(Float.NegativeInfinity)

  //返回得分最高的标签
  def getLabel: T = labels(0)

  def getLabel(i: Int): T = labels(i)

  def size = labels.size

  def getScore(i: Int) = scores(i)

  def set(i: Int, label2: T, d: Float) {
    labels(i) = label2
    scores(i) = d
  }

  def add(label: T, score: Float): Int = {
    var ret = -1 //返回的index
    if (n > 0) {
      //从得分中找到第一个大于score的
      val i = findFirstMatch(scores, i => {
        score > scores(i)
      })
      if (i < n) {
        //插入，将最后一个挤出去
        for (j <- n - 2 to i by -1) {
          scores(j + 1) = scores(j)
          labels(j + 1) = labels(j)
        }
        scores(i) = score
        labels(i) = label
        ret = i
      }
    }
    ret
  }

  //归一到0，1区间
  def normalize() {
    var base = 1f
    if (scores(0) > 0.0f) {
      base = scores(0) / 2
    }
    val sum = scores.map(s => {
      math.exp(s / base)
    }).sum

    for (i <- 0 until scores.length) {
      scores(i) = (scores(i) / sum).toFloat
    }

  }

  override def toString() = {
    val sb = new StringBuilder()
    for (i <- 0 until labels.length) {
      sb.append(labels(i))
      sb.append(" ")
      sb.append(scores(i))
      sb.append("\n")
    }
    sb.toString()
  }

  private def findFirstMatch(array: Array[Float], f: (Int => Boolean)): Int = {
    for (i <- 0 until array.length) {
      if (f(i)) {
        return i
      }
    }
    array.length
  }
}
