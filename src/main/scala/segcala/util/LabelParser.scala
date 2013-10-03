package segcala.util

import segcala.classifier.Predict
import segcala.model.alphabet.LabelAlphabet


/**
 * Date: 13-9-22
 * Time: 上午10:46
 * @author zhangyf
 */
object LabelParser {

  def parse(res: Predict[Array[Int]], labels: LabelAlphabet): Predict[Array[String]] = {
    val result = new Predict[Array[String]]()

    for(i <- 0 until res.size){
      val preds: Array[Int] = res.getLabel(i)
      val l = labels.lookupStrings(preds)
      result.set(i, l, res.getScore(i))
    }

    result
  }

}
