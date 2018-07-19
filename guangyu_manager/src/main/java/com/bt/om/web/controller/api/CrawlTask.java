package com.bt.om.web.controller.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.bt.om.system.GlobalVariable;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.HttpcomponentsUtil;

public class CrawlTask {
	private static final Logger logger = Logger.getLogger(CrawlTask.class);

	private static String taskUrl = ConfigUtil.getString("crawl.task.send.domain")
			+ ConfigUtil.getString("crawl.task.send.url");
	private static String loadDataUrl = ConfigUtil.getString("crawl.task.send.domain")
			+ ConfigUtil.getString("crawl.task.fetch.url");
	// 间隔多长时间，查询爬虫任务是否完成
	private static int sleepTime = 500;
	static {
		if ("on".equals(ConfigUtil.getString("is_test_evn"))) {
			taskUrl = ConfigUtil.getString("crawl.task.send.domain.test") + ConfigUtil.getString("crawl.task.send.url");
			loadDataUrl = ConfigUtil.getString("crawl.task.send.domain.test")
					+ ConfigUtil.getString("crawl.task.fetch.url");
		}
	}

	private String sendTask(String url) {
		List<NameValuePair> nvpList = new ArrayList<>();
		nvpList.add(new BasicNameValuePair("url", url));
		String retStr = "";
		try {
			String ret = HttpcomponentsUtil.postReq(nvpList, taskUrl);
			// System.out.println(ret);
			TaskBeanRet taskBeanRet = GsonUtil.GsonToBean(ret, TaskBeanRet.class);
			// System.out.println(taskBean.getMap().get("sign"));
			retStr = taskBeanRet.getRet().getMap().get("sign") + ";" + taskBeanRet.getRet().getMap().get("type");
			System.out.println("发送任务后返回值="+retStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retStr;
	}

	private String loadData(String params) {
		List<NameValuePair> nvpList = new ArrayList<>();
		nvpList.add(new BasicNameValuePair("sign", params.split(";")[0]));
		nvpList.add(new BasicNameValuePair("type", params.split(";")[1]));
		String ret = "";
		try {
			ret = HttpcomponentsUtil.postReq(nvpList, loadDataUrl);
			logger.info(ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public TaskBean getProduct(String url) {
		String params = sendTask(url);
		if (StringUtils.isEmpty(params)) {
			TaskBean taskBean = new TaskBean();
			taskBean.setMsg("");
			taskBean.setSucc(false);
			return taskBean;
		}
		String productInfo = loadData(params);
		TaskBeanRet taskBeanRet = GsonUtil.GsonToBean(productInfo, TaskBeanRet.class);
		int i = 0;
		while (true) {
			// 连续多少次查询后仍然查不到数据就退出
			if (i >= Integer.parseInt(GlobalVariable.resourceMap.get("task.info.check.num"))) {
				break;
			}
			if (taskBeanRet.getRet().getSucc() == true) {
				break;
			} else {
				try {
					Thread.sleep(sleepTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				productInfo = loadData(params);
				taskBeanRet = GsonUtil.GsonToBean(productInfo, TaskBeanRet.class);
			}
			i++;
		}
		return taskBeanRet.getRet();
	}

	public static void main(String[] args) {
		CrawlTask crawlTask = new CrawlTask();
		TaskBean taskBean = crawlTask.getProduct("https://detail.tmall.com/item.htm?id=565344963620");
		System.out.println(taskBean.getSucc());
	}

}
