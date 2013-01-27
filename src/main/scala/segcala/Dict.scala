package segcala

import io.Source
import com.google.common.io.Resources
import collection.mutable
import collection.mutable.ListBuffer
import com.typesafe.config.ConfigFactory


object Dict {

  //以首字符为key，value为树节点
  val dictionary = mutable.HashMap[Char, TreeNode]()
  sealed trait DictType

  //字符，记录其 Largest sum of degree of morphemic freedom of one-character words
  final case class CharDict() extends DictType
  final case class WordDict() extends DictType

  /**
   * 加载字典，使用前需显示调用一次
   */
  def load(){
    val config = ConfigFactory.load()
    val wordFile = config.getString("dict.wordFile")
    val charFile = config.getString("dict.charFile")
    Dict.loadDic(Resources.getResource(charFile).getFile, CharDict())
    Dict.loadDic(Resources.getResource(wordFile).getFile, WordDict())
  }

  private def loadDic(file: String, dictType: DictType) {
    for (line <- Source.fromFile(file).getLines()) {
      if (!line.contains("#")) {
        dictType match {
          case WordDict() => addWord(line.trim)
          case CharDict() => {
            val lArr = line.trim.split(" ")
            lArr.length match {
              case 2 => addWord(lArr(0), lArr(1).toInt)
              case 1 => addWord(lArr(0), 0)
            }
          }
        }
      }
    }
  }

  /**
   * 添加词
   * @param word 需要添加的词
   * @param freq Degree of Morphemic Freedom of One-Character, 单字才可能有
   */
  def addWord(word: String, freq: Int = 0) {
    val l = word.toList
    val opNode = search(l) orElse {
      val tn = if (l.length == 1) new TreeNode(l.head, 0, true, freq) else new TreeNode(l.head, 0, false, freq)
      dictionary(l.head) = tn
      Some(tn)
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

  /**
   * 查找字符串从指定偏移量起可以匹配的词
   * @param fragment  字符串
   * @param offset 偏移量
   * @return
   */
  def findMatchWords(fragment: List[Char], offset: Int): List[Word] = {
    val c = fragment(offset)
    var opNode = dictionary.get(c)
    var wordList = new ListBuffer[Word]
    if (opNode != None) {
      if (opNode.get.leaf) wordList += new Word(fragment, offset, 1, opNode.get.frequency)
      for (i <- offset + 1 until fragment.length) {
        if (opNode != None) {
          opNode = opNode.get.searchSubNodesForChar(fragment(i))
          if (opNode != None && opNode.get.leaf) {
            //println("add word: " + fragment.slice(offset, i+1))
            wordList += new Word(fragment, offset, i + 1 - offset)
          }
        }
      }
    }

    if (wordList.length == 0) {
      wordList += new Word(fragment, offset, 1)
    }

    wordList.toList
  }

  /**
   * 查找某词语在树中的位置，返回已存在的公共前缀的最后一个节点。返回None表示首字在词典中也不存在
   * @param l 待查找的词
   * @return
   */
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

    val node = dictionary.get(l.head)
    node match {
      case None => None
      case _ => searchTree(node.get, l.tail)
    }
  }

}

/**
 * 字典树节点
 * @param c 当前节点对应的字符
 * @param level 层级
 * @param leaf  是否为叶节点
 * @param frequency  Degree of Morphemic Freedom of One-Character, 单字才有
 */
class TreeNode(val c: Char, val level: Int, var leaf: Boolean, var frequency: Int) {
  def this(c: Char, level: Int, leaf: Boolean) = this(c, level, leaf, 0)

  //子节点
  private var subNodes = new ListBuffer[TreeNode]

  def addSubNode(node: TreeNode) {
    subNodes += node
    //TODO: sort
    //subNodes.sort((n1, n2) => (n1.c - n2.c)<0)
  }

  def searchSubNodesForChar(ch: Char): Option[TreeNode] = {
    //TODO: binary search
    subNodes.find(n => n.c == ch)
  }

}