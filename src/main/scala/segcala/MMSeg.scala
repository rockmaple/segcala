package segcala


/**
 * Created by IntelliJ IDEA.
 * User: rockmaple
 */

object MMSeg{

  def loadDict = {
    Dict.load()
  }

  def seg(fragment: TextFragment): List[Word] = {
    var words: List[Word] = List()
    while (!fragment.isFinish) {
      val chunk = Algorithm.seg(fragment)
      chunk.words.foreach(w => words = w :: words)
    }
    words
  }
}

