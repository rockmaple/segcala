package segcala

import com.google.inject.name.Named
import com.google.inject.Inject
import org.springframework.core.io.DefaultResourceLoader
import java.io.File
import io.Source
import segcala.Dict.{WordDict, CharDict, UnitDict}

/**
 * Created by IntelliJ IDEA.
 * User: rockmaple
 */


class Dict @Inject()(@Named("wordFiles") val wordFiles: String,
                     @Named("charFiles") val charFiles: String,
                     @Named("unitFiles") val unitFiles: String) {
  val loader = new DefaultResourceLoader()

  Dict.loadDic(loader.getResource(charFiles).getFile, CharDict())
  Dict.loadDic(loader.getResource(wordFiles).getFile, WordDict())
  Dict.loadDic(loader.getResource(unitFiles).getFile, UnitDict())
}


object Dict {
  import scala.collection.mutable.Map
  val dictionary = Map[Char, TreeNode]()

  sealed trait DictType
  final case class CharDict() extends DictType
  final case class WordDict() extends DictType
  final case class UnitDict() extends DictType

  private def loadDic(file: File, dictType: DictType) {
    for (line <- Source.fromFile(file).getLines) {
      if (!line.contains("#")) {
        dictType match {
          case WordDict() => {
            addWord(line.trim, 0)
          }
          case CharDict() => {
            val lArr = line.trim.split(" ")
            lArr.length match {
              case 2 => {
                addWord(lArr(0), lArr(1).toInt)
              }
              case 1 => {
                addWord(lArr(0), 0)
              }
            }
          }
          case UnitDict() => {

          }
        }
      }
    }
  }

  def addWord(word: String) {
    addWord(word, 0)
  }

  def addWord(word: String, freq: Int) {
    val l = word.toList
    var opNode = search(l)
    if (opNode == None) {
      val tn = if (l.length == 1) new TreeNode(l.head, 0, true, freq) else new TreeNode(l.head, 0, false, freq)
      dictionary(l.head) = tn
      opNode = Some(tn)
    }

    var node = opNode.get
    if (node.level == l.length - 1) {
      node.leaf = true
    } else {
      for (i <- node.level + 1 until l.length) {
        val tn = if (i == l.length - 1) new TreeNode(l(i), i, true, freq) else new TreeNode(l(i), i, false, freq)
        node.addSubNode(tn)
        node = tn
      }
    }
  }

  def findMatchWords(fragment: List[Char], offset: Int): List[Word] = {
    val c = fragment(offset)
    var opNode = dictionary.get(c)
    var wordList: List[Word] = List()
    if (opNode != None) {
      if (opNode.get.leaf) wordList = new Word(fragment, offset, 1, opNode.get.frequency) :: wordList
      for (i <- offset + 1 until fragment.length) {
        if (opNode != None) {
          opNode = opNode.get.searchSubNodesForChar(fragment(i))
          if (opNode != None && opNode.get.leaf) {
            //println("add word: " + fragment.slice(offset, i+1))
            wordList = new Word(fragment, offset, i + 1 - offset) :: wordList
          }
        }
      }
    }

    if(wordList.length == 0){
      wordList = new Word(fragment, offset, 1) :: wordList
    }

    wordList
  }

  //查找某词语在树中的位置，返回已存在的公共前缀的最后一个节点。返回None表示首字在词典中也不存在
  private def search(l: List[Char]): Option[TreeNode] = {

    def searchTree(n: TreeNode, l: List[Char]): Option[TreeNode] = {
      l.length match {
        case 0 => Some(n)
        case _ => {
          val tn = n.searchSubNodesForChar(l.head)
          tn match {
            case None => Some(n)
            case _ => searchTree(tn.get, l.tail)
          }
        }
      }
    }

    var node = dictionary.get(l.head)
    node match {
      case None => None
      case _ => searchTree(node.get, l.tail)
    }
  }

}

//frequency: Degree of Morphemic Freedom of One-Character, 单字才有
class TreeNode(val c: Char, val level: Int, var leaf: Boolean, var frequency: Int) {
  def this(c: Char, level: Int, leaf: Boolean) = this (c, level, leaf, 0)

  private var subNodes = List[TreeNode]()

  def addSubNode(node: TreeNode) {
    subNodes = node :: subNodes
    //TODO: sort
    //subNodes.sort((n1, n2) => (n1.c - n2.c)<0)
  }

  def searchSubNodesForChar(ch: Char): Option[TreeNode] = {
    //TODO: binary search
    subNodes.find(n => n.c == ch)
  }

}