
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.type.NullType;

import msra.nlp.seg.*;

public class SegFeature {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		
		Path featurePath = Paths.get("./data/feature.txt");
		Path outputPath = Paths.get("./output/featureSeged2.seg");
		File featureFile = featurePath.toFile();
		File outputFile = outputPath.toFile();
		FileInputStream fileInputStream = new FileInputStream(featureFile);
		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileInputStream));
		BufferedWriter fileWrtier = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
		String line;
		String[] array;
		Segmenter seg = new Segmenter();
		
/*		String[] test = {"夏延山核战碉堡	夏延山 核战 碉堡 美国 科罗拉多州 军事 基地 高强度 名 官员 战争 花岗岩 山体 挖空 洞穴 指挥所 弹簧 橡胶垫 核弹 粮食 饮水 医疗 电源 北美 空防 司令部 功能 美苏 格局 能力 政府 活力 营运 中心 钢铁 大楼 三层楼 事件 有所 	夏延山核战碉堡美国科罗拉多州一座防核战的军事指挥基地，50年代修建后运作至今。确保在高强度核战下还能保存6000名重要官员生存在其中数个月，指挥反击战争。堡中位于花岗岩山体挖空的洞穴，指挥所下面有巨大的弹簧和橡胶垫，能抗击核弹的直接命中。有充分粮食饮水和医疗品、电源和北美空防司令部的主要功能设备。50年代在美苏冷战格局下，相互保证毁灭，美国为确保第二击能力和政府存活力开始修建，1958年5月12日开始营运，整个中心共由15幢山中钢铁大楼组成，其中12幢为三层楼，其余为二层楼，911事件后有所扩建和更新。	http://rdf.basekb.com/ns/m.01hj5_",
					"双杠	双杠 男子 竞技体操 项目 金属 架子 平行 木头 塑料 典型 动作 臂 运动员 弧形 下法 杠的 比赛项目 	双杠是男子竞技体操项目之一。金属的架子支撑两条平行的木头、塑料或合成金属制成的杠。一套典型的双杠动作包括在支撑位置、倒立位置和挂臂位置的转换；运动员要在这些位置做摆动，摆越、屈伸、弧形摆动、回环、空翻和静止等动作。最后，整套动作的下法要求必须站在杠的一侧。双杠于1896年被列为奥运会比赛项目。	http://rdf.basekb.com/ns/m.01hj6r",
					"突击队员	突击队员 美国 动作 电影 马克 莱斯特 斯蒂芬 德 索萨 编剧 西尔 沃任 制片人 阿诺德 施瓦辛格 道恩 主演 主要演员 艾莉 莎 米兰 詹姆斯 奥尔森 丹 哈达 弗农 威尔斯 比尔 杜克 大卫 帕特里克 凯利 查尔斯 梅斯 哈克 主体 天 地点 加利福尼亚州 洛杉矶 影片 商业 票房 土星 奖 视觉效果 奖提名 卡梅伦 异形 评论 方面 霍纳 	《突击队员》是一部于1985年上映的美国动作电影，由马克·莱斯特执导，斯蒂芬·E.德·索萨编剧，乔·西尔沃任制片人，阿诺德·施瓦辛格和瑞伊·道恩·冲主演，其他主要演员还包括艾莉莎·米兰诺、詹姆斯·奥尔森、丹·哈达亚、弗农·威尔斯、比尔·杜克、大卫·帕特里克·凯利和查尔斯·梅斯哈克。电影的主体拍摄工作从1985年4月22日开始，一共进行了45天，主要拍摄地点集中在加利福尼亚州的洛杉矶周边。影片上映后商业上非常成功，以1000万美元的预算获得了超过5700万美元的票房收入。并获得了土星奖的最佳视觉效果奖提名，不过最终不敌詹姆斯·卡梅伦执导的《异形II》。电影在评论方面也还算成功，詹姆斯·霍纳谱写了电影的配乐。	http://rdf.basekb.com/ns/m.01hjmv"};
		for (String item : test) {
			String[] testArray = item.split("\t");
			String text = seg.segText(testArray[2]);
			System.out.println(text);
		}
*/
		//fileReader.s
		int i=0;
		while((line = fileReader.readLine())!= null){
			array = line.split("\t");
			line = seg.segText(array[2]);
			fileWrtier.write(line+"\n");
			i++;
			if(i%1000 == 0)
			{
				System.out.printf("Seg %d passages", i);
			}
		}
		fileReader.close();
		fileWrtier.close();
		
	}

}
