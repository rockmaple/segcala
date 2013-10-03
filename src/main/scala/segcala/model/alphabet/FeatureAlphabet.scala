package segcala.model.alphabet

/**
 * Date: 13-9-22
 * Time: 下午10:25
 * @author zhangyf
 */
trait FeatureAlphabet extends Alphabet{

  def lookupIndex(str: String, indent: Int): Int

}
