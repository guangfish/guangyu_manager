package com.bt.om;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;

import com.bt.om.selenium.ProductUrlTransLocal;
import com.bt.om.server.EmbbedJetty;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.HttpcomponentsUtil;
import com.bt.om.web.controller.api.TkInfoTaskRet;

public class GuangyuBooter {
	private static final Logger logger = Logger.getLogger(GuangyuBooter.class);
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public static void main(String[] args) {
		//启动远程任务获取线程
		if ("on".equals(ConfigUtil.getString("if.start.remote.get.task"))) {
			schedule();
		}
		
		EmbbedJetty.main(args);		
	}

	public static void getTask() {
		List<NameValuePair> nvpList = new ArrayList<>();
		String ret = "";
		String remoteTaskUrl = ConfigUtil.getString("crawl.task.send.domain")
				+ ConfigUtil.getString("remote.task.fetch.url");
		if ("on".equals(ConfigUtil.getString("is_test_evn"))) {
			remoteTaskUrl = ConfigUtil.getString("crawl.task.send.domain.test")
					+ ConfigUtil.getString("remote.task.fetch.url");
		}
		try {
			ret = HttpcomponentsUtil.sendHttps(nvpList, remoteTaskUrl);
//			System.out.println(ret);
			TkInfoTaskRet tkInfoTaskRet = GsonUtil.GsonToBean(ret, TkInfoTaskRet.class);
			if (tkInfoTaskRet.getRet() != null) {
				ProductUrlTransLocal.put(tkInfoTaskRet.getRet());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void schedule() {
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
//					logger.info("获取远程查询任务...");
					getTask();
				} catch (Exception e) {
					logger.error("获取远程查询任务 error:[{}]", e);
				}
			}
		}, 20000, 300, TimeUnit.MILLISECONDS);
	}
}
