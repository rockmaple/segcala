package segcala.model

import segcala.template._
import segcala.classifier.LinearViterbi
import com.google.common.io.{Closeables, Resources}
import java.io._
import java.util.zip.{GZIPOutputStream, GZIPInputStream}
import segcala.model.alphabet.{LabelAlphabet, StringFeatureAlphabet}

/**
 * Date: 13-9-21
 * Time: 上午9:01
 * @author zhangyf
 */
class ModelLoader {

  var fetureAlphabet: alphabet.StringFeatureAlphabet = _
  var labelAlphabet: alphabet.LabelAlphabet = _
  var templates: TemplateGroup = _

  var inferencer: LinearViterbi = _

  def loadModel(fileName: String) {
    val in = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(Resources.getResource(fileName).getFile()))))
    try {

      this.templates = in.readObject().asInstanceOf[TemplateGroup]
      this.fetureAlphabet = in.readObject().asInstanceOf[StringFeatureAlphabet]
      this.labelAlphabet = in.readObject().asInstanceOf[LabelAlphabet]
      this.inferencer = in.readObject().asInstanceOf[LinearViterbi]


      /*val templates = in.readObject().asInstanceOf[segcala.v2.template.TemplateGroup]
      val fetureAlphabet = in.readObject().asInstanceOf[segcala.v2.dictionary.alphabet.StringFeatureAlphabet]
      val labelAlphabet = in.readObject().asInstanceOf[segcala.v2.dictionary.alphabet.LabelAlphabet]
      val inferencer = in.readObject().asInstanceOf[segcala.v2.classifier.LinearViterbi]

      val templateList = ListBuffer[Template]()
      templates.templates.foreach(t => {
          if(t.isInstanceOf[segcala.v2.template.BasicTemplate]){
            val t1 = t.asInstanceOf[segcala.v2.template.BasicTemplate]
            templateList.append(new BasicTemplate(t1.id, t1.template, t1.order,t1.dim, t1.vars, t1.minLength))
          }else if(t.isInstanceOf[segcala.v2.template.CharClassTemplate]){
            val t1 = t.asInstanceOf[segcala.v2.template.CharClassTemplate]
            val charClassDicts = new Array[CharClassDict](t1.dicts.size)
            for (i <- 0 until t1.dicts.size) {
              val ccd = new CharClassDict(t1.dicts(i).name, t1.dicts(i).dict)
              charClassDicts(i) = ccd
            }
            val tpl = new CharClassTemplate(t1.id, charClassDicts)
            templateList.append(tpl)
          }else if(t.isInstanceOf[segcala.v2.template.CustomTemplate]){
            val t1 = t.asInstanceOf[segcala.v2.template.CustomTemplate]
            templateList.append(new CustomTemplate(t1.id))
          }
      })

      this.templates = new TemplateGroup(templateList.toList, templates.gid, templates.base, templates.maxOrder, templates.numStates, templates.offset)
      val origionData = fetureAlphabet.data
      val toData = new TObjectIntCustomHashMap[String](new StringFeatureHashStrategy, 10, 0.5f, -1)
      toData.putAll(origionData)
      this.fetureAlphabet = new StringFeatureAlphabet(toData, fetureAlphabet.frozen, fetureAlphabet.last)

      this.labelAlphabet = new LabelAlphabet(labelAlphabet.data, labelAlphabet.index, labelAlphabet.frozen)

      this.inferencer = new LinearViterbi(this.templates, inferencer.ysize, inferencer.weights)*/

    }
    catch {
      case  e: Exception =>{
        e.printStackTrace()
      }
    }
    finally {Closeables.close(in, false)}


  }

  def writeObject(){
    val out: ObjectOutputStream = new ObjectOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream("seg.model"))))
    out.writeObject(this.templates)
    out.writeObject(this.fetureAlphabet)
    out.writeObject(this.labelAlphabet)
    out.writeObject(this.inferencer)
    out.close
  }


}

object ModelLoader{
  def main(args: Array[String]){
    /*val dictLoader = new ModelLoader
    dictLoader.loadModel("models/segment.m")
    dictLoader.writeObject()*/

    /*val in = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream("seg.model"))))


    val templates = in.readObject().asInstanceOf[TemplateGroup]
    val fetureAlphabet = in.readObject().asInstanceOf[StringFeatureAlphabet]
    val labelAlphabet = in.readObject().asInstanceOf[LabelAlphabet]
    val inferencer = in.readObject().asInstanceOf[LinearViterbi]

    in.close()*/

    println("done")
}

}
