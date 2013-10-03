package segcala.classifier

import segcala.data.TextData

/**
 * Date: 13-9-21
 * Time: 下午5:00
 * @author zhangyf
 */
trait Inferencer extends Serializable{

  val weights: Array[Float]
  val numStates: Int

  def getBest(textData: TextData): Predict[Array[Int]]

}
