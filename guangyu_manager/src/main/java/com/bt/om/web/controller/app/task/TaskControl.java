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
import com.bt.om.service.ITkInfoTaskService;
import com.bt.om.service.impl.TkInfoTaskService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.RegexUtil;
import com.bt.om.util.StringUtil;
import com.bt.om.web.controller.app.vo.AppCrawlBean;

import redis.clients.jedis.ShardedJedis;

public class TaskControl {
	private static final Logger logger = Logger.getLogger(TaskControl.class);
	private static int sleepTime = 500;

	private ITkInfoTaskService tkInfoTaskService = ContextLoader.getCurrentWebApplicationContext()
			.getBean(TkInfoTaskService.class);

	private JedisPool jedisPool = ContextLoader.getCurrentWebApplicationContext().getBean(JedisPool.class);

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
				if("0".equals(resultMap.get("status"))){
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
//		sign="bd0dd1cdb08a48418b60973f07c2aac1";
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

		// 任务入队列
		Queue.put(tkInfoTask);

		return map;
	}

	private Map<String, String> loadData(String sign) {
		ShardedJedis jedis = jedisPool.getResource();
		String data=jedis.get(sign);
		jedis.close();
//		TkInfoTask tkInfoTask = tkInfoTaskService.selectBySign(sign);
		Map<String, String> map = null;
		if (data != null) {
			logger.info("从redis中查询到APP端推送的数据");
			logger.info(data);
			AppCrawlBean appCrawlBean = GsonUtil.GsonToBean(data, AppCrawlBean.class);
			TkInfoTask tkInfoTask = new TkInfoTask();
			try {
				String tklStr=appCrawlBean.getData();
				if(StringUtil.isEmpty(tklStr)){
					map = new HashMap<>();
					map.put("status", "0");
					return map;
				}
				
				String tklOld=appCrawlBean.getTklStr();
				jedis = jedisPool.getResource();
				System.out.println(tklOld.hashCode());
				String imgUrl = jedis.get(tklOld.hashCode()+""); 
				System.out.println(imgUrl);
				jedis.close();
				
				String tklSymbolsStr = GlobalVariable.resourceMap.get("tkl.symbol");				
				String sellNumStr=appCrawlBean.getSellNum();
				String sellNum="";
				String commissionStr=appCrawlBean.getCommission();
				String commission="";

				logger.info("sellNum="+sellNumStr);
				logger.info("commission="+commissionStr);
				
				String prodcutName=tklStr.substring(0, tklStr.indexOf("【"));
				String price="0";
				String quanHou="";
				String quan="0";
				String tkl="";
				String tkLink="";
				List<String[]> lists=RegexUtil.getListMatcher(tklStr, "【在售价】(.*?)元");
				if(lists.size()>0){
					price=(lists.get(0))[0];
				}
				lists=RegexUtil.getListMatcher(tklStr, "【券后价】(.*?)元");
				if(lists.size()>0){
					quanHou=(lists.get(0))[0];
				}
				lists=RegexUtil.getListMatcher(tklStr, "【下单链接】(.*?)--");
				if(lists.size()>0){
					tkLink=(lists.get(0))[0];
				}
				lists=RegexUtil.getListMatcher(commissionStr, "（预计￥(.*?)）");
				if(lists.size()>0){
					commission=(lists.get(0))[0];
				}
				lists=RegexUtil.getListMatcher(sellNumStr, "已售(.*?)件");
				if(lists.size()>0){
					sellNum=(lists.get(0))[0];
				}
				
				for(String symbol:tklSymbolsStr.split(";")){
					String reg = symbol + ".*" + symbol;
					Pattern pattern = Pattern.compile(reg);
					Matcher matcher = pattern.matcher(tklStr);
					if (matcher.find()) {
						lists=RegexUtil.getListMatcher(tklStr, symbol+"(.*?)"+symbol);
						if(lists.size()>0){
							tkl=symbol+(lists.get(0))[0]+symbol;
						}
						break;
					}
				}
				if(StringUtil.isNotEmpty(quanHou)){
					quan=Float.parseFloat(price)-Float.parseFloat(quanHou)+"";
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
				if(StringUtil.isEmpty(quanHou)){
					tkInfoTask.setRate(((double) (Math.round(Double.parseDouble(commission)/Double.parseDouble(price) * 100)) / 100));
				}else{
					tkInfoTask.setRate(((double) (Math.round(Double.parseDouble(commission)/Double.parseDouble(quanHou) * 100)) / 100));
				}
				int sellNumInt=0;
	            if(sellNum.contains("万")){
	            	sellNumInt=Integer.parseInt(sellNum.replace("万", ""))*10000;
	            }else{
	            	sellNumInt=Integer.parseInt(sellNum);
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
		}else{
			logger.info(sign+"APP端尚未返回");
		}
		return map;
	}
}
