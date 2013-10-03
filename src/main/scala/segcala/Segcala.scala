package segcala

import com.google.common.base.Preconditions
import segcala.model.alphabet.LabelAlphabet
import segcala.data.processor.{GenDictData, DataProcessor, Seq2FeatureSeq, String2Seq}
import segcala.classifier.{ConstraintViterbi, Inferencer}
import segcala.model.ModelLoader
import scala.collection.mutable.ListBuffer
import segcala.data.{TextFragWithSeqWithDataWithDic, TextFragWithSeqWithData, TextFrag}
import segcala.util.LabelParser
import segcala.dictionary.Dictionary

object Segcala {

  val dicFile = "models/seg.model"

  val inst = {
    val segcala = new Segcala
    segcala.init(dicFile)
    segcala
  }

  def segment(s: String): String = {
    inst.doSegment(s)
  }

  def main(args: Array[String]) {
    val s = Segcala.segment("行政审批机构与政府本身之间的博弈。行政审批机构隶属于政府，受政府节制，但现实中二者很难一条心。政府本身（政府首脑是其代表）基于经济和社会发展的压力")

    println("s: " + s)
  }

}

class Segcala {

  private var labelAlphabet: LabelAlphabet = _
  private var dataProcessor: DataProcessor = _
  private var inferencer: Inferencer = _

  def init(dicFile: String) {
    val loader: ModelLoader = new ModelLoader()
    loader.loadModel(dicFile)
    this.inferencer = ConstraintViterbi(loader.inferencer)
    this.labelAlphabet = loader.labelAlphabet

    val dictionary = Dictionary(false)
    dictionary.addSegDict(List("一条心", "行政审批机构", "社会发展"))

    this.dataProcessor = {
      val genDic = new GenDictData(dictionary, labelAlphabet, None)
      val seqToFeaureSeq = new Seq2FeatureSeq(loader.templates, loader.fetureAlphabet, loader.labelAlphabet, Some(genDic))
      val string2Seq = new String2Seq(true, Some(seqToFeaureSeq))
      string2Seq
    }

  }

  def doSegment(s: String): String = {
    val textFrag = new TextFrag(s)
    val processed = dataProcessor.process(textFrag).get.asInstanceOf[TextFragWithSeqWithDataWithDic]
    val res = inferencer.getBest(processed)
    val pred = LabelParser.parse(res, labelAlphabet)
    val sArr = pred.getLabel(0).asInstanceOf[Array[String]]
    val s1 = segToString(processed.source, sArr, " ")
    s1
  }

  def segToString(data: Array[Array[String]], labels: Array[String], delim: String): String = {
    Preconditions.checkArgument(data.length > 0)
    val len = data(0).length
    val sb = new StringBuilder
    for (j <- 0 until len - 1) {
      val label = labels(j)
      val w = data(0)(j)
      sb.append(w)
      if (label == "E" || label == "S") sb.append(delim)
    }
    sb.append(data(0)(len - 1))
    sb.toString()
  }

  def setToList(data: Array[Array[String]], labels: Array[String]): List[String] = {
    Preconditions.checkArgument(data.length > 0)
    val result = ListBuffer[String]()
    val cur = new StringBuilder
    for (i <- 0 until data.length) {
      val label = labels(i)
      val w = data(0)(i)
      if (data(1)(i) == "B") {
        if (!cur.isEmpty) {
          result.append(cur.toString())
          cur.clear()
        }
      } else {
        cur.append(w)
        if (label == "E" || label == "S") {
          result.append(cur.toString())
          cur.clear()
        }
      }
    }
    if (!cur.isEmpty) result.append(cur.toString())
    result.toList
  }

}
