package segcala.fnlp;


import java.io.File;
import java.util.ArrayList;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import edu.fudan.ml.types.Dictionary;
import edu.fudan.nlp.cn.tag.CWSTagger;

/**
 * 分词使用示例
 * @author xpqiu
 *
 */
public class ChineseWordSegmentation {
	/**
	 * 主程序
	 * @param args 
	 * @throws Exception
	 * @throws  
	 */
	public static void main(String[] args) throws Exception {

		/*System.out.println("不使用词典的分词：");
		String s = tag.tag(str);
		System.out.println(s);
		
		//设置英文预处理
		tag.setEnFilter(true);
		s = tag.tag(str);
		System.out.println(s);*/
//		tag.setEnFilter(false);
		System.out.println("\n设置临时词典：");
		ArrayList<String> al = new ArrayList<String>();
		al.add("政府本身");
		Dictionary dict = new Dictionary(false);
		dict.addSegDict(al);
        CWSTagger tag = new CWSTagger("models/seg.m");
        String str = "行政审批机构与政府本身之间的博弈"; //Files.toString(new File(Resources.getResource("news.txt").getFile()), Charsets.UTF_8);
		tag.setDictionary(dict);
		String s = tag.tag(str);
		System.out.println(s);

	}

}
