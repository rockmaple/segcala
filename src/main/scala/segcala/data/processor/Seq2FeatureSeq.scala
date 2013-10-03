package segcala.data.processor

import segcala.data.{TextFragWithSeqWithData, TextFragWithSeq, TextData}
import com.google.common.base.Preconditions
import com.typesafe.scalalogging.slf4j.Logging
import segcala.template.TemplateGroup
import segcala.model.alphabet.{LabelAlphabet, FeatureAlphabet}

/**
 * Date: 13-10-2
 * Time: 上午8:38
 * @author zhangyf
 */
class Seq2FeatureSeq(val templates: TemplateGroup, features: FeatureAlphabet, labels: LabelAlphabet, override val next: Option[DataProcessor]) extends DataProcessor with Logging {
  def process(textData: TextData): Option[TextData] = {
    textData match {
      case TextFragWithSeq(origion, source) => {
        Preconditions.checkArgument(source.length > 0)
        val length = source(0).length
        val data = Array.ofDim[Int](length, templates.size())
        for (i <- 0 until length) {
          for (j <- 0 until templates.size()) {
            data(i)(j) = templates.templates(j).generateAt(source, features, i, Array[Int](labels.size))
          }
        }
        val result = TextFragWithSeqWithData(origion, source, data)
        next match {
          case Some(x) => x.process(result)
          case None => {
            logger.info("this is the last processor: Seq2FeatureSeq")
            Some(result)
          }
        }
      }
    }
  }
}
