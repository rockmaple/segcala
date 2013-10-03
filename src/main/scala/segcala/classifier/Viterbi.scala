package segcala.classifier

import com.typesafe.scalalogging.slf4j.Logging
import segcala.data.{TextFragWithSeqWithDataWithDic, TextFragWithSeqWithData, TextData}
import segcala.template.TemplateGroup

trait AbstractViterbi extends Inferencer {

  def orders: Array[Int]

  def numTemplates: Int

  def ysize: Int

  def templates: TemplateGroup

}

/**
 * @param templates
 * @param ysize label(BMES)的数量，这里为4
 * @param weights
 */
@SerialVersionUID(8526647820685280539L)
class LinearViterbi(val templates: TemplateGroup, val ysize: Int, val weights: Array[Float]) extends AbstractViterbi with Logging {

  val orders = templates.orders
  val numTemplates = templates.size()
  val numStates = templates.numStates

  def getBest(textData: TextData): Predict[Array[Int]] = {
    textData match {
      case TextFragWithSeqWithData(origion, source, data) => {
        val lattice = initialLattice(textData)
        doForwardViterbi(lattice)
        getPath(lattice)
      }
      case TextFragWithSeqWithDataWithDic(origion, source, data, dictData) => {
        val lattice = initialLattice(textData)
        doForwardViterbi(lattice)
        getPath(lattice)
      }
    }
  }

  def doForwardViterbi(lattice: Array[Array[Node]]) {
    //logger.debug(lattice.deep.mkString("\n"))
    //logger.debug("------------")
    for (i <- 1 until lattice.length) {
      for (j <- 0 until lattice(i).length) {
        if (lattice(i)(j) != null) {
          var bestScore = Float.NegativeInfinity
          var bestPath = -1
          val prevLat = lattice(i - 1)
          for (k <- 0 until prevLat.length) {
            if (prevLat(k) != null) {
              val score = prevLat(k).score + lattice(i)(j).trans(k)
              if (score > bestScore) {
                bestScore = score
                bestPath = k
              }
            }
          }
          bestScore += lattice(i)(j).score
          lattice(i)(j).setScoreAndPath(bestScore, bestPath)
        }
      }
    }
    //logger.debug(lattice.deep.mkString("\n"))

  }

  def getPath(lattice: Array[Array[Node]]): Predict[Array[Int]] = {

    val res: Predict[Array[Int]] = new Predict[Array[Int]]

    if (lattice.length > 0) {
      val lastLaNodeArr = lattice(lattice.length - 1)
      val max: (Node, Int) = lastLaNodeArr.zipWithIndex.maxBy(_._1.score)
      //最后一行分数最高的
      var cur = max._2

      val path = new Array[Int](lattice.length)
      path(lattice.length - 1) = cur

      for (i <- lattice.length - 1 until 0 by -1) {
        cur = lattice(i)(cur).prev
        path(i - 1) = cur
      }
      res.add(path, max._1.score)
    }
    res
  }

  def initialLattice(textData: TextData): Array[Array[Node]] = {
    val textFrag = textData.asInstanceOf[TextFragWithSeqWithData]
    val data = textFrag.data
    val length = data.length
    val lattice = new Array[Array[Node]](length)
    for (i <- 0 until length) {
      lattice(i) = new Array[Node](ysize)
      for (j <- 0 until ysize) {
        lattice(i)(j) = new Node(ysize)
        for (k <- 0 until orders.length) {
          if (data(i)(k) != -1 && data(i)(k) < weights.length) {
            orders(k) match {
              case 0 => {
                lattice(i)(j).score += weights(data(i)(k) + j)
              }
              case 1 => {
                var offset = j
                for (p <- 0 until ysize) {
                  lattice(i)(j).trans(p) += weights(data(i)(k) + offset)
                  offset += ysize
                }
              }
            }
          }
        }
      }
    }
    lattice
  }
}

object ConstraintViterbi {
  def apply(viterbi: LinearViterbi, newysize: Int): ConstraintViterbi = {
    new ConstraintViterbi(viterbi.templates, newysize, viterbi.ysize, viterbi.weights)
  }

  def apply(viterbi: LinearViterbi): ConstraintViterbi = {
    ConstraintViterbi(viterbi, viterbi.ysize)
  }
}

class ConstraintViterbi(templates: TemplateGroup, newysize: Int, ysize: Int, weights: Array[Float]) extends LinearViterbi(templates, ysize, weights) with Logging {

  override def initialLattice(textData: TextData): Array[Array[Node]] = {
    val textFrag = textData.asInstanceOf[TextFragWithSeqWithDataWithDic]
    val data = textFrag.data
    val length = data.length
    val dicData = textFrag.dicData

    //logger.debug("data: " + data.deep.mkString("\n"))
    //logger.debug("dicData: " + dicData.deep.mkString("\n"))

    val lattice = new Array[Array[Node]](length)
    for (i <- 0 until length) {
      lattice(i) = new Array[Node](newysize)
      for (j <- 0 until ysize) {
        if (dicData(i)(j) == 0) {
          lattice(i)(j) = new Node(newysize)
          for (k <- 0 until orders.length) {
            if (data(i)(k) != -1) {
              orders(k) match {
                case 0 => {
                  lattice(i)(j).score += weights(data(i)(k) + j)
                }
                case 1 => {
                  if(i > 0){
                    for (p <- 0 until ysize) {
                      val offset = p*ysize + j
                      lattice(i)(j).trans(p) += weights(data(i)(k) + offset)
                    }
                  }
                }
              }
            }
          }
        }
      }
      for(q <- ysize until newysize){
        if(dicData(i)(q) == 0){
          lattice(i)(q) = new Node(newysize)
        }
      }
    }
    lattice
  }

}

class Node(val n: Int) {
  var base = 0f
  var score = 0f
  var prev = -1
  val trans = Array.fill[Float](n)(0f)

  def setScoreAndPath(score: Float, path: Int) {
    this.score = score
    this.prev = path
  }

  override def toString = {
    score.toString
  }
}

