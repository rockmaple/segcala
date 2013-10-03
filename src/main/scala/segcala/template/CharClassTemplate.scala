package segcala.template

import segcala.model.CharClassDict
import segcala.model.alphabet.FeatureAlphabet


/**
 * Date: 13-9-22
 * Time: 下午6:54
 * @author zhangyf
 */
class CharClassTemplate(val id: Int, val dicts: Array[CharClassDict]) extends Template{
  def getOrder: Int = 0

  def generateAt(data: Array[Array[String]], features: FeatureAlphabet, pos: Int, numLabels: Array[Int]): Int = {
    val sb = new StringBuilder
    sb.append(id)
    sb.append(":")
    val c = data(0)(pos).charAt(0)
    for(i <- 0 until dicts.length){
      if(dicts(i).contains(c)){
        sb.append("/")
        sb.append(dicts(i).name)
      }
    }
    if(sb.length < 3){
      return -1
    }

    features.lookupIndex(sb.toString(),numLabels(0))
  }

  def getVars: Array[Int] = Array(0)
}
