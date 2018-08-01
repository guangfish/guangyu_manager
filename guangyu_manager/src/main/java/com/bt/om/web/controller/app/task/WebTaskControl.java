package com.bt.om.web.controller.app.task;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoader;

import com.bt.om.cache.JedisPool;
import com.bt.om.entity.TkInfoTask;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.StringUtil;

public class WebTaskControl {
	private static final Logger logger = Logger.getLogger(WebTaskControl.class);
	private static int sleepTime = 500;

	private static JedisPool jedisPool = ContextLoader.getCurrentWebApplicationContext().getBean(JedisPool.class);

	// 商品信息查询 
	public Map<String, String> getProduct(String tkl) {
		Map<String, String> paramsMap = sendTask(tkl);
		Map<String, String> resultMap = loadData(paramsMap.get("sign"));
		int i = 0;
		while (true) {
			// 连续多少次查询后仍然查不到数据就退出
			if (i >= Integer.parseInt(GlobalVariable.resourceMap.get("task.info.check.num"))) {
				break;
			}
			if (resultMap != null) {
				if("1".equals(resultMap.get("status"))){
					return null;
				}
				break;
			} else {
				try {
					Thread.sleep(sleepTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				resultMap = loadData(paramsMap.get("sign"));
			}
			i++;
		}
		return resultMap;
	}

	// 发送任务
	public Map<String, String> sendTask(String tkl) {
		Map<String, String> map = new HashMap<>();
		String sign = StringUtil.getUUID();
		map.put("sign", sign);
		map.put("type", "2");
		map.put("status", "0");

		TkInfoTask tkInfoTask = new TkInfoTask();
		tkInfoTask.setProductUrl(tkl);
		tkInfoTask.setSign(sign);
		tkInfoTask.setType(2);
		tkInfoTask.setStatus(0);
		tkInfoTask.setCreateTime(new Date());
		tkInfoTask.setUpdateTime(new Date());

		// 队列中任务小于3时入队列
		if (WebQueue.getSize() < 3) {
			// 任务入队列
			WebQueue.put(tkInfoTask);
		} else {
			tkInfoTask.setStatus(1);
			jedisPool.putInCache("", sign, tkInfoTask, 60);
			logger.info("队列中任务大于3【" + WebQueue.getSize() + "】");
		}

		return map;
	}

	private Map<String, String> loadData(String sign) {
		Map<String, String> map = null;
		Object tkInfoTaskObj = jedisPool.getFromCache("", sign);
		if (tkInfoTaskObj != null) {
			map = new HashMap<>();
			TkInfoTask tkInfoTask = (TkInfoTask) tkInfoTaskObj;
			map.put("imgUrl", tkInfoTask.getProductImgUrl());
			map.put("shopName", tkInfoTask.getShopName());
			map.put("sign", sign);
			map.put("productName", tkInfoTask.getProductName());
			map.put("productUrl", tkInfoTask.getProductUrl());
			map.put("quanUrl", tkInfoTask.getQuanUrl());
			map.put("commission", "" + tkInfoTask.getCommision());
			map.put("price", "" + tkInfoTask.getPrice());
			map.put("rate", tkInfoTask.getRate() + "");
			map.put("sellNum", tkInfoTask.getSales() + "");
			map.put("tkUrl", tkInfoTask.getTkurl());
			map.put("tkl", tkInfoTask.getTcode());
			map.put("tklquan", tkInfoTask.getQuanCode());
			map.put("quanMianzhi", "" + tkInfoTask.getQuanMianzhi());
			map.put("status", "" + tkInfoTask.getStatus());
		} else {
			logger.info(sign + "web端尚未返回");
		}
		return map;
	}
}
