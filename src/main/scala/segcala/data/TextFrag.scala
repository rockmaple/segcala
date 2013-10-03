package segcala.data

/**
 * Date: 13-10-2
 * Time: 上午8:02
 * @author zhangyf
 */
trait TextData

case class TextFrag(origion: String) extends TextData

case class TextFragWithSeq(origion: String, source: Array[Array[String]]) extends TextData

case class TextFragWithSeqWithData(origion: String, source: Array[Array[String]], data: Array[Array[Int]]) extends TextData

case class TextFragWithSeqWithDataWithDic(origion: String, source: Array[Array[String]], data: Array[Array[Int]], dicData: Array[Array[Int]]) extends TextData
