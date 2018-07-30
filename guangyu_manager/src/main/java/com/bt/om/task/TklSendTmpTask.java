package com.bt.om.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bt.om.util.HttpcomponentsUtil;
import com.bt.om.util.NumberUtil;

/**
 * 测试淘口令定时发送
 */
@Component
public class TklSendTmpTask {
	private static final Logger logger = Logger.getLogger(TklSendTmpTask.class);


	// 每隔一段时间进行一次邀请用户的核实
	@Scheduled(cron = "0/20 * * * * ?")
	public void sendTkl() {
		logger.info("测试淘口令定时发送");
		List<String> tkls = new ArrayList<>();
		tkls.add("【清风抽纸巾批发整箱24包餐巾纸抽纸500家庭装原木纯品官方旗舰店】http://m.tb.cn/h.3XF0od5 点击链接，再选择浏览器咑閞；或復·制这段描述€IWUkbZ76TDE€后到淘♂寳♀");
		tkls.add("【夏季2018新款粉色气质显瘦长裙仙气冷淡风V领长款雪纺连衣裙女夏】http://m.tb.cn/h.32ctbMU 点击链接，再选择浏览器咑閞；或復·制这段描述€AXG4bZGYwFa€后到淘♂寳♀");
		tkls.add("【2018夏季新款女装韩版无袖棉麻连衣裙欧洲站长款亚麻度假显瘦长裙】http://m.tb.cn/h.32csYbS 点击链接，再选择浏览器咑閞；或復·制这段描述€qClrbZGcfIC€后到淘♂寳♀");
		tkls.add("【妃之影2018夏装新款女装气质条纹中长裙短袖系带显瘦衬衫领连衣裙,妃之影2018夏装新款女装，气质条纹短袖连衣裙，显瘦系带连衣裙，清新的条纹，丰富了视觉效果，凸显衣着品味】，http://m.tb.cn/h.323GJrP 点击链接，再选择浏览器咑閞；或復·制这段描述€KM7rbZGcEU1€后咑閞淘♂寳♀");
		tkls.add("【Queen2018夏季新品女装V领短袖收腰大摆长裙纯色压褶中长款连衣裙】http://m.tb.cn/h.32cuAGd 点击链接，再选择浏览器咑閞；或復·制这段描述€zYgmbZG2I8m€后到淘♂寳♀");
		int i=0;
		
		i=NumberUtil.getRandomInt(0, tkls.size()-1);
		sendTask(tkls.get(i));
	}
	
	private static String sendTask(String url) {
		String taskUrl="https://www.guangfish.com/app/api/sendTask";
		List<NameValuePair> nvpList = new ArrayList<>();
		nvpList.add(new BasicNameValuePair("url", url));
		String retStr = "";
		try {
			String ret = HttpcomponentsUtil.sendHttps(nvpList, taskUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retStr;
	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((TklSendTmpTask) ctx.getBean("tklSendTmpTask")).sendTkl();
	}
}
