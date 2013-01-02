package segcala

import java.util.Properties
import com.google.inject.name.Names
import com.google.inject.{AbstractModule}
import java.io.PushbackReader
import collection.mutable.{Queue}
import io.Source

/**
 * Created by IntelliJ IDEA.
 * User: rockmaple
 */

class MMSeg(val reader: PushbackReader){

  var textBuffer: StringBuilder = new StringBuilder
  var curFragment: Option[TextFragment] = None
  var words: Queue[Word] = new Queue
  var curIndex: Int = -1
  

  def next(): Option[Word] = {

    words.length match {
      case 0 => None
      case _ =>{
        textBuffer.setLength(0)
        var data = -1
        var toRead = true
        while(toRead && (data = readNext()) != -1){
          toRead = false
          var t = Character.getType(data)
          t match {
            case Character.OTHER_LETTER => handleOtherLetter(data)
            case (Character.UPPERCASE_LETTER|Character.LOWERCASE_LETTER|Character.TITLECASE_LETTER|Character.MODIFIER_LETTER) => {
              
            }
            case _ => toRead = true
          }
        }
      }
    }

  }

  private def handleOtherLetter(data){
     textBuffer.appendCodePoint(data)
     readCharsByType(Character.OTHER_LETTER)
     textFragment = creageTextFragment()
     textBuffer.setLength(0)
  }

  private def readCharsByType(charType: Int){
    var data = -1
    while((data = readNext) != -1){
      var t = Character.getType(data)
      if(t == charType){
        textBuffer.append(data)
      }else{
        pushBack(data)
      }
    }
  }

  private def creageTextFragment(){

  }

  private def readNext(): Int = {
    var d = reader.read
    if (d > -1) {
      curIndex ++
      d = Character.toLowerCase(d)
    }
    d
  }

  private def pushBack(data: Int) {
    curIndex --
    reader.unread(data)
  }
  

  def seg(fragment: TextFragment): List[Word] = {
      var words:List[Word] = List()
      while(!fragment.isFinish){
        val chunk = Algorithm.seg(fragment)
        chunk.words.foreach(w => words = w::words)
      }
      words
  }

}

class SegModule extends AbstractModule {
  override def configure() {

    val properties = new Properties()
    val loader = classOf[SegModule].getClassLoader
    properties.load(loader.getResource("wordseg.properties").openStream)
    Names.bindProperties(binder(), properties);
    
  }

}
