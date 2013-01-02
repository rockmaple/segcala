package segcala

import org.junit._
import Assert._
import com.google.inject.{Injector, Guice}

@Test
class AppTest {

  @Test
  def testDict(){
    Dict.addWord("研究")
    Dict.addWord("生命")
    Dict.addWord("科学")
    Dict.addWord("研究生")
    Dict.addWord("命", 1234)

    val words = Dict.findMatchWords("研究生命科学".toList, 0)

    words.foreach(w => println(w.value))

    assertTrue(true)
  }

  @Test
  def testSeg(){
    val inj:Injector = Guice.createInjector(new SegModule())
    inj.getInstance(classOf[Dict])

    /*Dict.addWord("研究")
    Dict.addWord("生命")
    Dict.addWord("科学")
    Dict.addWord("科")
    Dict.addWord("研究生")
    Dict.addWord("命", 1234)*/

    val words = MMSeg.seg(new TextFragment("研究生命科学"))
    words.foreach(w => println(w.value))

  }

}


