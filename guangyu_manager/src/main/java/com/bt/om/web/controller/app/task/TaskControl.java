package com.bt.om.web.controller.app.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoader;

import com.bt.om.cache.JedisPool;
import com.bt.om.entity.TkInfoTask;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.RegexUtil;
import com.bt.om.util.StringUtil;
import com.bt.om.web.controller.app.vo.AppCrawlBean;

public class TaskControl {
	private static final Logger logger = Logger.getLogger(TaskControl.class);
	private static int sleepTime = 500;

	private static JedisPool jedisPool = ContextLoader.getCurrentWebApplicationContext().getBean(JedisPool.class);

	// 商品信息查询
	public Map<String, String> getProduct(String tkl) {
		Map<String, String> paramsMap = sendTask(tkl);
		Map<String, String> resultMap = loadData(paramsMap.get("sign"));
        String taskinfochecknumStr="";
        int taskinfochecknum=30;
		int i = 0;
		while (true) {
			taskinfochecknumStr=GlobalVariable.resourceMap.get("task.info.check.num");
			if(StringUtil.isNotEmpty(taskinfochecknumStr)){
				taskinfochecknum=Integer.parseInt(taskinfochecknumStr);
			}
			try {
				// 连续多少次查询后仍然查不到数据就退出
				if (i >= taskinfochecknum) {
					break;
				}
				if (resultMap != null) {
					if ("0".equals(resultMap.get("status"))) {
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
			} catch (Exception e) {
				e.printStackTrace();
			}
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

		// 队列中任务小于3时入队列，这里的逻辑应该不会执行，因为前面AppApiController中已经过滤了一下了
		if (Queue.getSize() < 3) {
			// 任务入队列
			Queue.put(tkInfoTask);
			logger.info(tkl + "入队列");
		} else {
			jedisPool.putInCache("", sign, "{\"tklStr\":\"" + tkl + "\",\"sign\":\"" + sign + "\"}", 60);
			logger.info("队列中任务大于3【" + Queue.getSize() + "】");
		}

		return map;
	}

	private Map<String, String> loadData(String sign) {
		String data = "";
		//根据sign从redis中获取APP推送的结果数据
		Object dataObj = jedisPool.getFromCache("", sign);
		if (dataObj != null) {
			data = (String) dataObj;
		}
		Map<String, String> map = null;
		if (StringUtil.isNotEmpty(data)) {
			logger.info("从redis中查询到APP端推送的数据");
			logger.info(data);
			AppCrawlBean appCrawlBean = GsonUtil.GsonToBean(data, AppCrawlBean.class);
			TkInfoTask tkInfoTask = new TkInfoTask();
			try {
				String tklStr = appCrawlBean.getData();
				//判断结果返回的数据中是否爬到了数据，如果没有则置状态为0，然后返回
				if (StringUtil.isEmpty(tklStr)) {
					map = new HashMap<>();
					map.put("status", "0");
					return map;
				}else{
					tklStr=tklStr.replace("\n", "");
				}
				logger.info(tklStr);

				//用转换前的淘口令获取图片地址
				String tklOld = appCrawlBean.getTklStr();
				Object imgUrlObj = jedisPool.getFromCache("", tklOld.hashCode());
				String imgUrl = "";
				if (imgUrlObj != null) {
					imgUrl = (String) imgUrlObj;
				}

				String tklSymbolsStr = GlobalVariable.resourceMap.get("tkl.symbol");
				String sellNumStr = appCrawlBean.getSellNum();
				String sellNum = "";
				String commissionStr = appCrawlBean.getCommission();
				String commission = "";

				logger.info("sellNum=" + sellNumStr);
				logger.info("commission=" + commissionStr);

				String prodcutName = tklStr.substring(0, tklStr.indexOf("【"));
				String price = "0";
				String quanHou = "";
				String quan = "0";
				String tkl = "";
				String tkLink = "";
				//以下的正则如果淘宝联盟发生了改变，那么这里也要改表
				List<String[]> lists = RegexUtil.getListMatcher(tklStr, "【在售价】(.*?)元");
				if (lists.size() > 0) {
					price = (lists.get(0))[0];
				}
				lists = RegexUtil.getListMatcher(tklStr, "【券后价】(.*?)元");
				if (lists.size() > 0) {
					quanHou = (lists.get(0))[0];
				}
				lists = RegexUtil.getListMatcher(tklStr, "【下单链接】(.*?)--");
				if (lists.size() > 0) {
					tkLink = (lists.get(0))[0];
				}
				logger.info("下单链接=="+tkLink);
				lists = RegexUtil.getListMatcher(commissionStr, "（预计￥(.*?)）");
				if (lists.size() > 0) {
					commission = (lists.get(0))[0];
				}
				lists = RegexUtil.getListMatcher(sellNumStr, "已售(.*?)件");
				if (lists.size() > 0) {
					sellNum = (lists.get(0))[0];
				}

				for (String symbol : tklSymbolsStr.split(";")) {
					String reg = symbol + ".*" + symbol;
					Pattern pattern = Pattern.compile(reg);
					Matcher matcher = pattern.matcher(tklStr);
					if (matcher.find()) {
						lists = RegexUtil.getListMatcher(tklStr, symbol + "(.*?)" + symbol);
						if (lists.size() > 0) {
							tkl = symbol + (lists.get(0))[0] + symbol;
						}
						break;
					}
				}
				if (StringUtil.isNotEmpty(quanHou)) {
					quan = Float.parseFloat(price) - Float.parseFloat(quanHou) + "";
				}

				tkInfoTask.setSign(sign);
				tkInfoTask.setProductImgUrl(imgUrl);
				tkInfoTask.setShopName("");
				tkInfoTask.setProductName(prodcutName);
				tkInfoTask.setPrice(Double.parseDouble(price));
				tkInfoTask.setTcode(tkl);
				tkInfoTask.setTkurl(tkLink);
				if (StringUtil.isNotEmpty(appCrawlBean.getQuan())) {
					tkInfoTask.setQuanMianzhi(Double.parseDouble(quan));
				}
				tkInfoTask.setCommision(((double) (Math.round(Double.parseDouble(commission) * 100)) / 100));
				if (StringUtil.isEmpty(quanHou)) {
					tkInfoTask.setRate(
							((double) (Math.round(Double.parseDouble(commission) / Double.parseDouble(price) * 100))
									/ 100));
				} else {
					tkInfoTask.setRate(
							((double) (Math.round(Double.parseDouble(commission) / Double.parseDouble(quanHou) * 100))
									/ 100));
				}
				int sellNumInt = 0;
				if (sellNum.contains("万")) {
					sellNumInt = (int) (Float.parseFloat(sellNum.replace("万", "")) * 10000);
				} else {
					sellNumInt = Integer.parseInt(sellNum);
				}
				tkInfoTask.setSales(sellNumInt);
				tkInfoTask.setStatus(0);
				tkInfoTask.setType(2);
				tkInfoTask.setCreateTime(new Date());
				tkInfoTask.setUpdateTime(new Date());
			} catch (Exception e) {
				e.printStackTrace();
			}

			map = new HashMap<>();
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
			map.put("status", "1");
		} else {
			logger.info(sign + "APP端尚未返回");
		}
		return map;
	}
}
