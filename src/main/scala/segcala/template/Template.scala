package segcala.template

import segcala.model.alphabet.FeatureAlphabet


/**
 * Date: 13-9-22
 * Time: 下午6:10
 * @author zhangyf
 */
trait Template extends Serializable{

  def getOrder(): Int

  def generateAt(data: Array[Array[String]], features: FeatureAlphabet, pos: Int, numLabels: Array[Int]): Int

  def getVars(): Array[Int]

}
