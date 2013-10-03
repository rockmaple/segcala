package segcala.model

import gnu.trove.set.hash.TCharHashSet

/**
 * Date: 13-9-22
 * Time: 下午6:07
 * @author zhangyf
 */
class CharClassDict(val name: String, val dict: TCharHashSet = new TCharHashSet()) extends Serializable{

  def contains(c:Char): Boolean = dict.contains(c)

}
