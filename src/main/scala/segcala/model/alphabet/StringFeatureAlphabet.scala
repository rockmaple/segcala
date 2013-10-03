package segcala.model.alphabet

import gnu.trove.map.custom_hash.TObjectIntCustomHashMap
import gnu.trove.strategy.HashingStrategy
import com.google.common.hash.Hashing
import com.google.common.base.Charsets
import com.typesafe.scalalogging.slf4j.Logging

/**
 * Date: 13-9-22
 * Time: 下午10:25
 * @author zhangyf
 */
object StringFeatureAlphabet{

}

@SerialVersionUID(-2482414318928581360L)
class StringFeatureAlphabet(val data: TObjectIntCustomHashMap[String], var frozen: Boolean = false, var last: Int = 0) extends FeatureAlphabet with Logging{

  def lookupIndex(str: String, indent: Int): Int = {
    if (indent < 1) throw new IllegalArgumentException("Invalid Argument in FeatureAlphabet: " + indent)
    var ret = data.get(str)
    //logger.debug("lookupIndex: " + ret + " str: " + str)
    if(ret == -1 && !frozen){
      this.synchronized{
        data.put(str, last)
        ret = last
        last += indent
      }
    }
    ret
  }

  def lookupIndex(str: String): Int = {
    lookupIndex(str, 1)
  }

  def size = data.size()

  def setStopIncrement(b: Boolean) {
    frozen = true
  }

}

class StringFeatureHashStrategy extends HashingStrategy[String]{
  def computeHashCode(obj: String): Int = {
    Hashing.murmur3_32().hashString(obj, Charsets.UTF_8).hashCode()
  }

  def equals(o1: String, o2: String): Boolean = o1 == o2
}
