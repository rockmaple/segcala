package segcala.data.processor

import segcala.data.TextData

/**
 * Date: 13-10-2
 * Time: 上午8:14
 * @author zhangyf
 */
trait DataProcessor {

  def process(textData: TextData): Option[TextData]

  //nex processor
  def next: Option[DataProcessor]
}
