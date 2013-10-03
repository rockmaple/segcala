package segcala.util

import com.google.common.base.Preconditions

object Chars {

  //半角全角数字或字符
  def isChar(c: Char) = Character.isDigit(c) || Character.isLowerCase(c) || Character.isUpperCase(c)

  def getStringType(str: String): StringType.Value = {
    val types = getType(str).distinct
    types.size match {
      case 1 => charToStringType(types(0))
      case _ => StringType.M
    }
  }

  def charToStringType(c: CharType.Value): StringType.Value = {
    c match {
      case CharType.D => StringType.D
      case CharType.C => StringType.C
      case CharType.L => StringType.L
      case CharType.B => StringType.B
      case _ => StringType.O
    }
  }

  def getType(str: String): Array[CharType.Value] = {
    Preconditions.checkNotNull(str)
    val result = new Array[CharType.Value](str.length)
    for(i <- 0 until str.length){
      val c = str.charAt(i)
      result(i) = getCharType(c)
    }
    result
  }

  def getCharType(c: Char): CharType.Value = {
    val charType = Character.getType(c)
    c match {
      case ch if Character.isLowerCase(ch) || Character.isUpperCase(ch) => CharType.L
      case ch if ch == 12288 || ch == 32 => CharType.B
      case ch if Character.isDigit(ch) => CharType.D
      case ch if (charType >= 20 && charType <= 30) => CharType.P
      case _ => CharType.C
    }
  }

}

object CharType extends Enumeration {
  val C, L, D, P, B = Value //汉字， 字母， 数字， 标点， 空格
}

object StringType extends Enumeration {
  val D, L, C, M, B, O = Value //纯数字, 纯字母, 纯汉字, 混合字符串, 空格, 其他，例如标点等
}
