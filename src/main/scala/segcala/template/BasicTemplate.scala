package segcala.template

import com.google.common.base.Preconditions
import segcala.model.alphabet.FeatureAlphabet
import com.typesafe.scalalogging.slf4j.Logging

/**
 *  0: %x[-1,0]%x[1,0]%y[0];
 *  1: %y[-1]%y[0]
 */
@SerialVersionUID(-8179293093870936617L)
class BasicTemplate(val id: Int,
                    val template: String,
                    val order: Int,
                    val dim: Array[Array[Int]],
                    val vars: Array[Int],
                    val minLength: Int = 0) extends Template with Logging{

  def getOrder: Int = order

  def generateAt(data: Array[Array[String]], features: FeatureAlphabet, pos: Int, numLabels: Array[Int]): Int = {
    Preconditions.checkArgument(numLabels.length == 1)
    Preconditions.checkArgument(data.length > 0)
    val length = data(0).length
    if (order > 0 && length == 1) {   //对于长度为1的序列，不考虑1阶以上特征
      return -1
    }
    val sb = new StringBuilder
    sb.append(id)
    sb.append(":")
    for (i <- 0 until dim.length) {
      val j = dim(i)(0)
      val k = dim(i)(1)      //
      if (k >= data.length) {
        return -1
      }
      val p = pos + j
      if (p < 0) {
        if (length < minLength) {
          return -1
        }
        for(q <- p until 0){
          sb.append("B")
        }
        sb.append("_")
      } else if (p >= length) {
        if (length < minLength) {
          return -1
        }
        for(q <- p to length by -1){
          sb.append("E")
        }
        sb.append("_")
      } else {
        sb.append(data(k)(p))
      }
      sb.append("/")
    }
    logger.debug("key: " + sb.toString())
    features.lookupIndex(sb.toString(), math.pow(numLabels(0), order + 1).toInt)
  }

  def getVars: Array[Int] = vars

}
