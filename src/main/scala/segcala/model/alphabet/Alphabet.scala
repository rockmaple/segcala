package segcala.model.alphabet

/**
 * Date: 13-9-22
 * Time: 下午10:23
 * @author zhangyf
 */
trait Alphabet extends Serializable{

  def lookupIndex(str: String): Int

  def size: Int

  def setStopIncrement(b: Boolean)

}
