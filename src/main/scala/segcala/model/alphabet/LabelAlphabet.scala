package segcala.model.alphabet

import gnu.trove.map.hash.{TIntObjectHashMap, TObjectIntHashMap}

/**
 * Date: 13-9-22
 * Time: 下午10:25
 * @author zhangyf
 */
object LabelAlphabet {
  def apply(): LabelAlphabet = {
    val data = new TObjectIntHashMap[String](10, 0.5f, -1)
    val index = new TIntObjectHashMap[String]()
    val frozen = false
    new LabelAlphabet(data, index, frozen)
  }
}

class LabelAlphabet(val data: TObjectIntHashMap[String], val index: TIntObjectHashMap[String], var frozen: Boolean) extends Alphabet {

  def lookupIndex(str: String): Int = {
    var ret = data.get(str)
    if (ret == -1 && !frozen) {
      this.synchronized {
        ret = index.size()
        data.put(str, ret)
        index.put(ret, str)
      }
    }
    ret
  }

  def lookupStrings(ids: Array[Int]): Array[String] = {
    val result = new Array[String](ids.length)
    for(i <- 0 until ids.length){
      result(i) = index.get(ids(i))
    }
    result
  }

  def size = data.size()

  def setStopIncrement(b: Boolean) {
    frozen = true
  }
}
