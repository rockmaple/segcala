package segcala.template

/**
 * Date: 13-9-22
 * Time: 下午6:59
 * @author zhangyf
 */
class TemplateGroup(val templates: List[Template],
                    val gid: Int,
                    val base: Array[Int],
                    val maxOrder: Int,
                    val numStates: Int,
                    val offset: Array[Array[Int]]) extends Serializable{

  val orders: Array[Int] = {
    val ords = new Array[Int](templates.length)
    for(i <- 0 until templates.length){
      ords(i) = templates(i).getOrder()
    }
    ords
  }

  def size() = templates.size

}
