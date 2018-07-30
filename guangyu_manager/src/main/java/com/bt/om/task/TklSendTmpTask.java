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
		tkls.add("€IWUkbZ76TDE€");
		tkls.add("€AXG4bZGYwFa€");
		tkls.add("€qClrbZGcfIC€");
		tkls.add("€KM7rbZGcEU1€");
		tkls.add("€zYgmbZG2I8m€");
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
