package com.bt.om.web.controller.app;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.cache.JedisPool;
import com.bt.om.entity.TkInfoTask;
import com.bt.om.selenium.ProductUrlTrans;
import com.bt.om.service.ITkInfoTaskService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.Img2Base64Util;
import com.bt.om.util.RegexUtil;
import com.bt.om.util.StringUtil;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.app.vo.AppCrawlBean;
import com.bt.om.web.controller.app.vo.AppCrawlTaskBean;

import redis.clients.jedis.ShardedJedis;

/**
 * APP端爬虫接口
 */
@Controller
@RequestMapping(value = "/app/api")
public class AppCawalTaskController extends BasicController {
	private static final Logger logger = Logger.getLogger(AppCawalTaskController.class);
	@Autowired
	private JedisPool jedisPool;
	@Autowired
	private ITkInfoTaskService tkInfoTaskService;

	// 获取验证码
	@RequestMapping(value = "/getTask", method = RequestMethod.POST)
	@ResponseBody
	public AppCrawlTaskBean getTask(Model model, HttpServletRequest request, HttpServletResponse response) {
		AppCrawlTaskBean appCrawlTaskBean = null;
		TkInfoTask tkInfoTask = null;
		Object object = ProductUrlTrans.getTkl();
		if (object != null) {
			appCrawlTaskBean=new AppCrawlTaskBean();
			tkInfoTask = (TkInfoTask) object;
			appCrawlTaskBean.setSign(tkInfoTask.getSign());
			appCrawlTaskBean.setTklStr(tkInfoTask.getProductUrl());
		}
		return appCrawlTaskBean;
	}

	@RequestMapping(value = "/pushData", method = RequestMethod.POST)
	@ResponseBody
	public Model pushData(Model model, HttpServletRequest request, HttpServletResponse response) {
		String data = request.getParameter("data");
		logger.info(data);
		AppCrawlBean appCrawlBean = GsonUtil.GsonToBean(data, AppCrawlBean.class);
		TkInfoTask tkInfoTask = new TkInfoTask();
		try {
			String tklOld=appCrawlBean.getTklStr();
			ShardedJedis jedis = jedisPool.getResource();
			System.out.println(tklOld.hashCode());
			String imgUrl = jedis.get(tklOld.hashCode()+"");
			System.out.println(imgUrl);
			jedis.close();
			
			String tklSymbolsStr = GlobalVariable.resourceMap.get("tkl.symbol");
			String tklStr=appCrawlBean.getTkl();
			String sign=appCrawlBean.getSign();
			String sellNum=appCrawlBean.getSellNum();
			String commission=appCrawlBean.getCommission();
			logger.info("sellNum="+sellNum);
			logger.info("commission="+commission);
			
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
			lists=RegexUtil.getListMatcher(tklStr, "【下单链接】(.*)");
			if(lists.size()>0){
				tkLink=(lists.get(0))[0];
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
			tkInfoTask.setCommision(0d);
			tkInfoTask.setRate(0d);
			tkInfoTask.setSales(0);
			tkInfoTask.setStatus(0);
			tkInfoTask.setType(2);
			tkInfoTask.setCreateTime(new Date());
			tkInfoTask.setUpdateTime(new Date());

			tkInfoTaskService.insertTkInfoTask(tkInfoTask);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}
}
