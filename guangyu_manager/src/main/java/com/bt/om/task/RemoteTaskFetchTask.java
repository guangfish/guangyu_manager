package com.bt.om.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bt.om.entity.TkInfoTask;
import com.bt.om.selenium.ProductUrlTrans;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.HttpcomponentsUtil;
import com.bt.om.web.controller.api.TaskBeanRet;
import com.bt.om.web.controller.api.TkInfoTaskRet;

@Component
public class RemoteTaskFetchTask {
	private static final Logger logger = Logger.getLogger(RemoteTaskFetchTask.class);

	@Scheduled(cron = "0/5 * * * * ?")
	public void getTask() {
		logger.info("定时获取远程商品佣金查询任务");
		List<NameValuePair> nvpList = new ArrayList<>();
		String ret = "";
		String remoteTaskUrl = ConfigUtil.getString("crawl.task.send.domain")
				+ ConfigUtil.getString("remote.task.fetch.url");
		if ("on".equals(ConfigUtil.getString("is_test_evn"))) {
			remoteTaskUrl = ConfigUtil.getString("crawl.task.send.domain.test")
					+ ConfigUtil.getString("remote.task.fetch.url");
		}
		try {
			ret = HttpcomponentsUtil.postReq(nvpList, remoteTaskUrl);
			TkInfoTaskRet tkInfoTaskRet = GsonUtil.GsonToBean(ret, TkInfoTaskRet.class);
//			System.out.println(ret);
			if(tkInfoTaskRet.getRet()!=null){
				ProductUrlTrans.put(tkInfoTaskRet.getRet());
//				System.out.println(tkInfoTaskRet.getRet().getProductUrl());
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((RemoteTaskFetchTask) ctx.getBean("remoteTaskFetchTask")).getTask();
	}
}
