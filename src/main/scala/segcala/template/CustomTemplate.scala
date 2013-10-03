package segcala.template

import segcala.model.alphabet.FeatureAlphabet

/**
 * Date: 13-9-22
 * Time: 下午6:47
 * @author zhangyf
 */
class CustomTemplate(val id: Int) extends Template {

  def getOrder(): Int = 0

  def generateAt(data: Array[Array[String]], features: FeatureAlphabet, pos: Int, numLabels: Array[Int]): Int = {
    val length = data(0).length
    if (pos + 1 >= length) {
      return -1
    }
    val sb = new StringBuilder
    sb.append(id)
    sb.append(":")
    val str1 = data(0)(pos)
    val str2 = data(0)(pos + 1)

    if(str1.length == 1 && str1 == str2){
      sb.append("T")
    } else{
      sb.append("F")
    }

    features.lookupIndex(sb.toString, numLabels(0))
  }

  def getVars(): Array[Int] = Array(0)
}
