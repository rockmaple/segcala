package segcala

import java.util.Properties
import com.google.inject.name.Names
import com.google.inject.{AbstractModule}
import java.io.PushbackReader
import collection.mutable.{Queue}
import io.Source
import collection.mutable

/**
 * Created by IntelliJ IDEA.
 * User: rockmaple
 */

object MMSeg{

  def seg(fragment: TextFragment): List[Word] = {
    var words: List[Word] = List()
    while (!fragment.isFinish) {
      val chunk = Algorithm.seg(fragment)
      chunk.words.foreach(w => words = w :: words)
    }
    words
  }

}

class SegModule extends AbstractModule {
  override def configure() {
    val properties = new Properties()
    val loader = classOf[SegModule].getClassLoader
    properties.load(loader.getResource("wordseg.properties").openStream)
    Names.bindProperties(binder(), properties)
  }

}
