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
import com.bt.om.web.controller.app.vo.BaseVo;

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
		logger.info("收到app任务获取请求");
		AppCrawlTaskBean appCrawlTaskBean = null;
		TkInfoTask tkInfoTask = null;
		Object object = ProductUrlTrans.getTkl();
		appCrawlTaskBean=new AppCrawlTaskBean();
		if (object != null) {			
			tkInfoTask = (TkInfoTask) object;
			appCrawlTaskBean.setStatus("1");
			appCrawlTaskBean.setSign(tkInfoTask.getSign());
			appCrawlTaskBean.setTklStr(tkInfoTask.getProductUrl());	
			logger.info("队列有任务返回");
		}else{
			appCrawlTaskBean.setSign("");
			appCrawlTaskBean.setStatus("0");
			appCrawlTaskBean.setTklStr("");
			logger.info("队列中无任务");
		}
		return appCrawlTaskBean;
	}

	@RequestMapping(value = "/pushData", method = RequestMethod.POST)
	@ResponseBody
	public Model pushData(Model model, HttpServletRequest request, HttpServletResponse response) {
		String data = request.getParameter("data");
		data="{\"data\": \"Queen2018夏季新品女装V领短袖收腰大摆长裙纯色压褶中长款连衣裙【在售价】338.00元【下单链接】http://m.tb.cn/h.3XA5P2R-----------------復·制这段描述，€xCYobZpohZ4€ ，咑閞【手机淘宝】即可查看\",\"tklStr\": \"【Queen2018夏季新品女装V领短袖收腰大摆长裙纯色压褶中长款连衣裙】http://m.tb.cn/h.3XAUhUY 点击链接，再选择浏览器咑閞；或復·制这段描述€hytQbZpSpPR€后到?淘♂寳♀?\",\"sign\": \"08e047db0ea74aac989945984f2c25e3\",\"commission\": \"（预计￥54.08）\",\"sellNum\": \"已售61件\",\"status\": \"1\"}";
		logger.info("收到APP端任务结果数据推送");
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
			
			imgUrl="http://img.alicdn.com/bao/uploaded/i2/3543248633/TB1H0FTonlYBeNjSszcXXbwhFXa_!!0-item_pic.jpg";
			
			String tklSymbolsStr = GlobalVariable.resourceMap.get("tkl.symbol");
			String tklStr=appCrawlBean.getData();
			tklStr= "Queen2018夏季新品女装V领短袖收腰大摆长裙纯色压褶中长款连衣裙【在售价】338.00元【下单链接】http://m.tb.cn/h.3XA5P2R-----------------復·制这段描述，€xCYobZpohZ4€ ，咑閞【手机淘宝】即可查看";
			String sign=appCrawlBean.getSign();
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
//			String commissionRate=GlobalVariable.resourceMap.get("commission.rate");
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

			tkInfoTaskService.insertTkInfoTask(tkInfoTask);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}
}
