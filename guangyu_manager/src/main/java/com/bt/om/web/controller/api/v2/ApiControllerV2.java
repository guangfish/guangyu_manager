package com.bt.om.web.controller.api.v2;

import com.bt.om.cache.JedisPool;
import com.bt.om.common.SysConst;
import com.bt.om.entity.ProductInfo;
import com.bt.om.entity.SearchRecord;
import com.bt.om.entity.TkInfoTask;
import com.bt.om.entity.TkOrderInput;
import com.bt.om.entity.TkOrderInputJd;
import com.bt.om.enums.ResultCode;
import com.bt.om.selenium.ProductUrlTrans;
import com.bt.om.service.IProductInfoService;
import com.bt.om.service.ISearchRecordService;
import com.bt.om.service.ITkInfoTaskService;
import com.bt.om.service.ITkOrderInputJdService;
import com.bt.om.service.ITkOrderInputService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.taobao.api.TaoKouling;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.RequestUtil;
import com.bt.om.util.SecurityUtil1;
import com.bt.om.util.StringUtil;
import com.bt.om.util.TaobaoSmsNewUtil;
import com.bt.om.vo.api.GetSmsCodeVo;
import com.bt.om.vo.api.ProductCommissionVo;
import com.bt.om.web.controller.api.v2.vo.CommonVo;
import com.bt.om.web.controller.api.v2.vo.ProductInfoVo;
//import com.bt.om.vo.api.UserOrderVo;
import com.bt.om.vo.web.ResultVo;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.api.CrawlTask;
import com.bt.om.web.controller.api.TaskBean;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import redis.clients.jedis.ShardedJedis;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
//import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v2/api")
public class ApiControllerV2 extends BasicController {
	private static final Logger logger = Logger.getLogger(ApiControllerV2.class);

	@Autowired
	private IProductInfoService productInfoService;

	@Autowired
	private ITkInfoTaskService tkInfoTaskService;
	
	@Autowired
	private ISearchRecordService searchRecordService;
	
	@Autowired
	private ITkOrderInputService tkOrderInputService;
	
	@Autowired
	private ITkOrderInputJdService tkOrderInputJdService;

	@Autowired
	private JedisPool jedisPool;

	// 获取验证码
	@RequestMapping(value = "/getSmsCode", method = RequestMethod.POST)
	@ResponseBody
	public Model getSmsCode(Model model, HttpServletRequest request, HttpServletResponse response) {
		String remoteIp=RequestUtil.getRealIp(request);
		CommonVo commonVo=new CommonVo();
		String mobile = "";
		String userId="";
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("userId") != null) {
				userId = obj.get("userId").getAsString();
				userId = SecurityUtil1.decrypts(userId);
				mobile=userId;
			}else{
				mobile = obj.get("mobile").getAsString();
			}			
		} catch (IOException e) {
			commonVo.setStatus("1");
			commonVo.setDesc("系统繁忙，请稍后再试");
			model.addAttribute("response", commonVo);
			return model;
		}

		// 手机号验证
		if (StringUtils.isEmpty(mobile)) {
			commonVo.setStatus("2");
			commonVo.setDesc("手机号为必填");
			model.addAttribute("response", commonVo);
			return model;
		}
		
		ShardedJedis jedis = jedisPool.getResource();
		if(jedis.exists(mobile)){
			commonVo.setStatus("3");
			commonVo.setDesc("请等待2分钟后再次发送短信验证码");
			model.addAttribute("response", commonVo);
			return model;
		}

		String vcode = getVcode(5);
		System.out.println(vcode);
		jedis.setex(mobile, 120, vcode);
		jedis.close();

		// 发送短信验证码
		if ("on".equals(ConfigUtil.getString("is.sms.send"))) {
			if(!remoteIp.equals(GlobalVariable.resourceMap.get("send_sms_ignoy_ip"))){
				TaobaoSmsNewUtil.sendSms("逛鱼返利", "SMS_125955002","vcode", vcode, mobile);
			}	
		}

		commonVo.setStatus("0");
		commonVo.setDesc("验证码发送成功");
		
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");
		model.addAttribute("response", commonVo);
		return model;
	}

	// 获取商品详情
	@RequestMapping(value = "/productInfo", method = RequestMethod.POST)
	@ResponseBody
	public Model productInfo(Model model, HttpServletRequest request, HttpServletResponse response) {
		ProductInfoVo productInfoVo=new ProductInfoVo();
		String userId="";
		@SuppressWarnings("unused")
		String imei="";
		String productUrl = "";
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if(obj.get("userId")!=null){
				userId = obj.get("userId").getAsString();
				userId=SecurityUtil1.decrypts(userId);
			}
			if(obj.get("productUrl")!=null){
				productUrl = obj.get("productUrl").getAsString();
			}			
			if(obj.get("imei")!=null){
				imei = obj.get("imei").getAsString();
			}
		} catch (IOException e) {
			productInfoVo.setStatus("1");
			productInfoVo.setDesc("系统繁忙，请稍后再试");
			model.addAttribute("response", productInfoVo);
			return model;
		}

		// 商品链接验证
		if (StringUtils.isEmpty(productUrl)) {
			productInfoVo.setStatus("2");
			productInfoVo.setDesc("商品链接为空");
			model.addAttribute("response", productInfoVo);
			return model;
		}		
		
		String tklSymbolsStr = GlobalVariable.resourceMap.get("tkl.symbol");
		String[] tklSymbols = tklSymbolsStr.split(";");
		for (String symbol : tklSymbols) {
			String reg0 = symbol + ".*" + symbol;
			Pattern pattern0 = Pattern.compile(reg0);
			Matcher matcher0 = pattern0.matcher(productUrl);
			if (matcher0.find()) {
				productUrl = TaoKouling.parserTkl(productUrl);
				logger.info("通过淘口令转换获得的商品链接==>" + productUrl);
				if (StringUtils.isEmpty(productUrl)) {
					productInfoVo.setStatus("3");
					productInfoVo.setDesc("商品链接解析失败");
					model.addAttribute("response", productInfoVo);
					return model;
				} else {
					Map<String, String> urlMap0 = StringUtil.urlSplit(productUrl);
					String puri=urlMap0.get("puri");
					String pid="";
					if(puri.contains("a.m.taobao.com")){
						pid=puri.substring(puri.lastIndexOf("/")+2, puri.lastIndexOf("."));
						productUrl = "https://item.taobao.com/item.htm" + "?id=" + pid;
					}else{
						productUrl = urlMap0.get("puri") + "?id=" + urlMap0.get("id");
					}
					logger.info("通过淘口令转换获得的商品缩短链接==>" + productUrl);
				}
				break;
			}
		}

		Map<String, String> urlMap = StringUtil.urlSplit(productUrl);
		String platform = "taobao";
		if (urlMap.get("puri").contains("taobao.com") || urlMap.get("puri").contains("tmall.com")) {
			platform = "taobao";
			productUrl=urlMap.get("puri")+"?id="+urlMap.get("id");
		} else if (urlMap.get("puri").contains("jd.com")) {
			platform = "jd";
		}else{
			productInfoVo.setStatus("4");
			productInfoVo.setDesc("不支持的商品链接地址");
			model.addAttribute("response", productInfoVo);
			return model;
		}
        
		ProductInfo productInfo = null;
		String uriProductId="";
		if ("taobao".equals(platform)) {
			// 判断链接中是否有ID
			if (StringUtils.isEmpty(urlMap.get("id"))) {
				productInfoVo.setStatus("5");
				productInfoVo.setDesc("商品ID为空");				
				model.addAttribute("response", productInfoVo);
				return model;
			}
			uriProductId=urlMap.get("id");
		}else if("jd".equals(platform)){
			String puri=urlMap.get("puri");
			//截取京东商品ID
			String action=puri.substring(puri.lastIndexOf("/")+1);
			if(action.contains(".")){
				uriProductId=puri.substring(puri.lastIndexOf("/")+1, puri.lastIndexOf("."));
			}else{
				uriProductId=action;
			}			
			if (StringUtils.isEmpty(uriProductId)) {
				productInfoVo.setStatus("5");
				productInfoVo.setDesc("商品ID为空");				
				model.addAttribute("response", productInfoVo);
				return model;
			}
		}
//		从数据库中查询是否已查询过改商品
		productInfo = productInfoService.getByProductId(uriProductId);
		Map<String, String> map = new HashMap<>();
		
		if (productInfo == null) {
			productInfo = new ProductInfo();
			CrawlTask crawlTask = new CrawlTask();
			TaskBean taskBean = null;
			//如果是淘宝搜索的参数是商品地址
			if("taobao".equals(platform)){
				taskBean = crawlTask.getProduct(productUrl);
			}
			//如果是京东，搜索的参数是链接中商品ID
			else if("jd".equals(platform)){
				taskBean = crawlTask.getProduct(uriProductId);
			}
			if (StringUtils.isNotEmpty(taskBean.getMap().get("goodUrl1"))
					|| StringUtils.isNotEmpty(taskBean.getMap().get("goodUrl2"))) {
				String goodUrl = StringUtils.isEmpty(taskBean.getMap().get("goodUrl1"))
						? taskBean.getMap().get("goodUrl2") : taskBean.getMap().get("goodUrl1");
				
				String productId = "";
				String productInfoUrl = "";
				if ("taobao".equals(platform)) {
					productId = urlMap.get("id");
					productInfoUrl = taskBean.getMap().get("url");
				} else if("jd".equals(platform)){
					productId = uriProductId;
					productInfoUrl = urlMap.get("puri");
				}else{
					productId="";
					productInfoUrl="";
				}
				productInfo.setProductId(productId);
				productInfo.setProductInfoUrl(productInfoUrl);
				String productImgUrl = taskBean.getMap().get("img");
				productInfo.setProductImgUrl(productImgUrl);
				
				String shopName = taskBean.getMap().get("shop");
				productInfo.setShopName(shopName);
				String productName = taskBean.getMap().get("title");
				productInfo.setProductName(productName);
				String tkLink = goodUrl;
				productInfo.setTkLink(tkLink);
				double price = Double.valueOf(taskBean.getMap().get("price").replace("￥", "").replace(",", ""));
				productInfo.setPrice(price);
				float incomeRate = Float.valueOf(taskBean.getMap().get("per").replace("%", ""));
				productInfo.setIncomeRate(incomeRate);
				float commission = Float.valueOf(taskBean.getMap().get("money").replace("￥", ""));
				productInfo.setCommission(commission);
				String couponLink = taskBean.getMap().get("quanUrl");
				productInfo.setCouponLink(couponLink);
				productInfo.setCouponPromoLink(couponLink);
				String sellNum = taskBean.getMap().get("sellNum");
				productInfo.setMonthSales(Integer.parseInt(sellNum));
				String tkl=taskBean.getMap().get("tkl");
				productInfo.setTkl(tkl);
				String tklquan=taskBean.getMap().get("tklquan");
				productInfo.setTklquan(tklquan);
				String quanMianzhi=taskBean.getMap().get("quanMianzhi");
				productInfo.setCouponQuan(quanMianzhi);
				productInfo.setIfvalid(2);
				productInfo.setSourcefrom(2);
				productInfo.setCreateTime(new Date());
				productInfo.setUpdateTime(new Date());
				try{
				    productInfoService.insertProductInfo(productInfo);		
				}catch(Exception e){
					logger.error(e.getMessage());
				}
				
				//插入搜索记录
				SearchRecord searchRecord=new SearchRecord();
				searchRecord.setMobile(userId);
				searchRecord.setProductId(productId);
				searchRecord.setMall(platform.equals("taobao")?1:2);
				searchRecord.setStatus(1);
				searchRecord.setTitle(productName);
				searchRecord.setCreateTime(new Date());
				searchRecord.setUpdateTime(new Date());
				searchRecordService.insert(searchRecord);
				
				map.put("imgUrl", productImgUrl);
				map.put("shopName", shopName);
				map.put("productName", productName);
				map.put("commission", "" + ((float) (Math.round(commission * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100)) / 100));
				map.put("price", "" + price);
				if(StringUtil.isNotEmpty(tklquan)){
					map.put("tkl", tklquan);
				}else{
					map.put("tkl", tkl);
				}
				map.put("per", ((float) (Math.round(incomeRate * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100)) / 100) + "");
				map.put("sellNum", sellNum + "");				
				map.put("quanMianzhi", "" +quanMianzhi);
				
				float fanli=((float) (Math.round(commission * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100)) / 100);
				float fanliMultiple=1;
				
				if(fanli<=1){
					fanliMultiple=Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1"));
				}else if(fanli>1 && fanli<=5){
					fanliMultiple=Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1-5"));
				}else if(fanli>5 && fanli<=10){
					fanliMultiple=Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.5-10"));
				}else if(fanli>10 && fanli<=50){
					fanliMultiple=Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.10-50"));
				}else if(fanli>50 && fanli<=100){
					fanliMultiple=Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.50-100"));
				}else if(fanli>100 && fanli<=500){
					fanliMultiple=Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.100-500"));
				}else{
					fanliMultiple=Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.500"));
				}
				map.put("fanliMultiple", fanliMultiple+"");			
			} else {
				model.addAttribute("response", productInfoVo);
				return model;
			}
		} else {
			//插入搜索记录
			SearchRecord searchRecord=new SearchRecord();
			searchRecord.setMobile(userId);
			searchRecord.setProductId(productInfo.getProductId());
			searchRecord.setMall(platform.equals("taobao")?1:2);
			searchRecord.setStatus(1);
			searchRecord.setTitle(productInfo.getProductName());
			searchRecord.setCreateTime(new Date());
			searchRecord.setUpdateTime(new Date());
			searchRecordService.insert(searchRecord);
			
			map.put("imgUrl", productInfo.getProductImgUrl());
			map.put("shopName", productInfo.getShopName());
			map.put("productName", productInfo.getProductName());
			map.put("commission", "" + ((float) (Math.round(productInfo.getCommission() * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100))
					/ 100));
			map.put("price", "" + productInfo.getPrice());
			if(StringUtil.isNotEmpty(productInfo.getTklquan())){
				map.put("tkl", productInfo.getTklquan());
			}else{
				map.put("tkl", productInfo.getTkl()==null?"":productInfo.getTkl());
			}
			map.put("per", ((float) (Math.round(productInfo.getIncomeRate() * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100)) / 100) + "");
			map.put("sellNum", productInfo.getMonthSales() + "");
			map.put("quanMianzhi", "" +productInfo.getCouponQuan());				
			
			float fanli=((float) (Math.round(productInfo.getCommission() * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100))
					/ 100);
			float fanliMultiple=1;
			if(fanli<=1){
				fanliMultiple=Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1"));
			}else if(fanli>1 && fanli<=5){
				fanliMultiple=Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1-5"));
			}else if(fanli>5 && fanli<=10){
				fanliMultiple=Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.5-10"));
			}else if(fanli>10 && fanli<=50){
				fanliMultiple=Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.10-50"));
			}else if(fanli>50 && fanli<=100){
				fanliMultiple=Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.50-100"));
			}else if(fanli>100 && fanli<=500){
				fanliMultiple=Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.100-500"));
			}else{
				fanliMultiple=Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.500"));
			}
			
			map.put("fanliMultiple", fanliMultiple+"");								
		}
		
		List<Map<String, String>> list =new ArrayList<>();
		list.add(map);
		
        //查询成功
		productInfoVo.setStatus("0");
		productInfoVo.setDesc("查询成功");
		productInfoVo.setMall(platform);
		productInfoVo.setData(list);
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");

		model.addAttribute("response", productInfoVo);
		return model;
	}

	// 佣金信息获取请求
	@RequestMapping(value = "/getCommission", method = RequestMethod.POST)
	@ResponseBody
	public Model getCommission(Model model, HttpServletRequest request, HttpServletResponse response) {
		ResultVo<ProductCommissionVo> result = new ResultVo<>();
		result.setCode(ResultCode.RESULT_SUCCESS.getCode());
		result.setResultDes("商品折扣信息获取成功");
		model = new ExtendedModelMap();
		@SuppressWarnings("unused")
		String user_id;
		String num_iids = "";
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			user_id = obj.get("user_id").getAsString();
			num_iids = obj.get("num_iids").getAsString();
		} catch (IOException e) {
			result.setCode(ResultCode.RESULT_FAILURE.getCode());
			result.setResultDes("系统繁忙，请稍后再试！");
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}

		// 商品ID验证
		if (StringUtils.isEmpty(num_iids)) {
			result.setCode(ResultCode.RESULT_FAILURE.getCode());
			result.setResultDes("商品ID为空！");
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}
		List<String> num_iids_list = java.util.Arrays.asList(num_iids.split(","));

		Map<String, Object> productIdsMap = new HashMap<>();
		List<String> list = new ArrayList<>();
		for (String productId : num_iids_list) {
			list.add(productId);
		}
		productIdsMap.put("list", list);

		List<ProductInfo> productInfoList = productInfoService.getByProductIds(productIdsMap);

		int productListSize = 0;
		if (productInfoList == null || productInfoList.size() <= 0) {
			// result.setCode(ResultCode.RESULT_FAILURE.getCode());
			// result.setResultDes("商品信息不存在！");
			// model.addAttribute(SysConst.RESULT_KEY, result);
			// return model;

		} else {
			productListSize = productInfoList.size();
		}
		// System.out.println(productInfoList.size());

		// 商品佣金一一对应
		List<Float> commissionList = new ArrayList<>();
		for (String productId : num_iids_list) {
			int size = 0;
			if (productListSize > 0) {
				for (ProductInfo productInfo : productInfoList) {
					if (productId.equals(productInfo.getProductId())) {
						logger.info("实际佣金=" + productInfo.getCommission());
						commissionList.add(((float) (Math
								.round(productInfo.getCommission() * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100))
								/ 100));
//						commissionList.add(((float) (Math
//								.round(productInfo.getCommission() * 1 * 100))
//								/ 100));
						break;
					} else {
						size = size + 1;
					}
				}
				if (size == productInfoList.size()) {
					commissionList.add(0f);
				}
			} else {
				commissionList.add(0f);
			}
		}

		String commissions = "";
		Float[] commissionArr = commissionList.toArray(new Float[commissionList.size()]);
		commissions = StringUtils.join(commissionArr, ",");
		logger.info("折后佣金=" + commissions);

		result.setResult(new ProductCommissionVo(commissions));
		model.addAttribute(SysConst.RESULT_KEY, result);
		// response.getHeaders().add("Access-Control-Allow-Credentials","true");
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");

		return model;
	}

	// 发送爬虫任务
	@RequestMapping(value = "/crawl/addtask", method = RequestMethod.POST)
	@ResponseBody
	public Model addTask(Model model, HttpServletRequest request, HttpServletResponse response) {
		TaskBean result = new TaskBean();
		result.setSucc(true);
		result.setMsg("");
		model = new ExtendedModelMap();
		String url = "";
		url = request.getParameter("url");
		if (StringUtils.isEmpty(url)) {
			return model;
		}
		Map<String, String> map = new HashMap<>();
		String sign = StringUtil.getUUID();
		map.put("sign", sign);
		map.put("type", "1");
		map.put("status", "0");

		TkInfoTask tkInfoTask = new TkInfoTask();
		tkInfoTask.setProductUrl(url);
		tkInfoTask.setSign(sign);
		tkInfoTask.setType(1);
		tkInfoTask.setStatus(0);
		tkInfoTask.setCreateTime(new Date());
		tkInfoTask.setUpdateTime(new Date());

		ProductUrlTrans.put(tkInfoTask);

		result.setMap(map);
		model.addAttribute(SysConst.RESULT_KEY, result);
		// response.getHeaders().add("Access-Control-Allow-Credentials","true");
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");

		return model;
	}

	// 获取爬虫任务结果
	@RequestMapping(value = "/crawl/fetchtask", method = RequestMethod.POST)
	@ResponseBody
	public Model fetchTask(Model model, HttpServletRequest request, HttpServletResponse response) {
		TaskBean result = new TaskBean();
		result.setSucc(true);
		result.setMsg("");
		model = new ExtendedModelMap();
		String sign = "";
		String type = "";
		sign = request.getParameter("sign");
		type = request.getParameter("type");
		if (StringUtils.isEmpty(sign) || StringUtils.isEmpty(type)) {
			return model;
		}
		Map<String, String> map = new HashMap<>();

		TkInfoTask tkInfoTask = tkInfoTaskService.selectBySign(sign);
		if (tkInfoTask == null) {
			result.setSucc(false);
			result.setMsg("");
		} else {
			if (tkInfoTask.getStatus() == 1) {
				result.setSucc(true);
				result.setMsg(
						"<div id='e-c' style='position:fixed;z-index:999999999;width:100%;height:100%;left:0;top:0;' align=center><div style='background:#000;width:100%;height:100%;opacity:0.2;'></div><div style='font-size:12px;width:330px;position:fixed;top:10%;left:38%;background:#fff;border-radius:10px;box-shadow:5px 5px 10px #888;'><h2 style='padding:5px;font-size:18px;'>该商品无返利</h2></div></div>");
			} else {
				map.put("img", tkInfoTask.getProductImgUrl());
				map.put("shop", tkInfoTask.getShopName());
				map.put("sign", sign);
				map.put("tkl1", tkInfoTask.getTcode());
				map.put("title", tkInfoTask.getProductName());
				map.put("url", tkInfoTask.getProductUrl());
				map.put("quanUrl", tkInfoTask.getQuanUrl());
				map.put("money", "￥" + tkInfoTask.getCommision());
				map.put("tagNum", "");
				map.put("price", "￥" + tkInfoTask.getPrice());
				map.put("tklquan", tkInfoTask.getQuanCode());
				map.put("tag", "");
				map.put("per", tkInfoTask.getRate() + "%");
				map.put("sellNum", tkInfoTask.getSales() + "");
				map.put("goodUrl1", tkInfoTask.getTkurl());
				map.put("tkl", tkInfoTask.getTcode());
				map.put("tklquan", tkInfoTask.getQuanCode());
				map.put("quanMianzhi", "" +tkInfoTask.getQuanMianzhi());

				StringBuffer sb = new StringBuffer();
				sb.append(
						"<div id='e-c' align=center></div><div style='font-size:12px;width:330px;top:10%;left:38%;background:#fff;border-radius:10px;box-shadow:5px 5px 10px #888;'><div><img src='");
				sb.append(tkInfoTask.getProductImgUrl());
				sb.append("'></div><div>");
				sb.append(tkInfoTask.getProductName());
				sb.append("</div><div style='height:20px;'><span style='float:left;'>商店：");
				sb.append(tkInfoTask.getShopName());
				sb.append("</span><span style='float:right;'>销量：");
				sb.append(tkInfoTask.getSales());
				sb.append("</span></div><div style='height: 20px;'><span style='float: left;'>价格：￥");
				sb.append(tkInfoTask.getPrice());
				sb.append("</span><span style='float: right;'>返现：￥");
				sb.append(tkInfoTask.getCommision());
				sb.append("(");
				sb.append(tkInfoTask.getRate());
				sb.append("%)</span></div><div id='btn-app'><a href='");
				sb.append(tkInfoTask.getTkurl());
				sb.append("'>推广链接</a>");
				if (StringUtils.isNotEmpty(tkInfoTask.getQuanUrl())) {
					sb.append(" | <a href='");
					sb.append(tkInfoTask.getQuanUrl());
					sb.append("'>优惠券</a>");
				}
				sb.append("</div><div style='color:red;'><br />如果有优惠券请先点优惠券获取，再点击优惠券下方的链接购买。</div></div></div>");
				result.setMsg(sb.toString());
			}
		}
		result.setMap(map);

		model.addAttribute(SysConst.RESULT_KEY, result);
		// response.getHeaders().add("Access-Control-Allow-Credentials","true");
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");

		return model;
	}
	
	// 从队列中获取任务
	@RequestMapping(value = "/gettask", method = RequestMethod.POST)
	@ResponseBody
	public Model getTask(Model model, HttpServletRequest request, HttpServletResponse response) {
		TkInfoTask tkInfoTask=null;
		Object object =ProductUrlTrans.get();
		if(object!=null){
			tkInfoTask=(TkInfoTask)object;
			tkInfoTask.setCreateTime(null);
			tkInfoTask.setUpdateTime(null);
		}
		model.addAttribute(SysConst.RESULT_KEY, tkInfoTask);
		return model;
	}
	
	// 任务执行完后的反馈
	@RequestMapping(value = "/receiveTaskFeedback", method = RequestMethod.POST)
	@ResponseBody
	public Model receiveTaskFeedback(Model model, HttpServletRequest request, HttpServletResponse response) {
		String gString=request.getParameter("gString");
		System.out.println(gString);		
		TkInfoTask tkInfoTask = GsonUtil.GsonToBean(gString, TkInfoTask.class);
		tkInfoTaskService.insertTkInfoTask(tkInfoTask);
		return model;
	}
	
	// 淘宝报表数据下载后的入库
	@RequestMapping(value = "/reportTb2Db", method = RequestMethod.POST)
	@ResponseBody
	public Model reportTb2Db(Model model, HttpServletRequest request, HttpServletResponse response) {
		String gString = request.getParameter("gString");
//		System.out.println("reportTb2Db=="+gString);
		List<TkOrderInput> tkOrderInputList = GsonUtil.fromJsonList(gString, TkOrderInput.class);
		tkOrderInputService.truncateTkOrderInput();
		for (TkOrderInput tkOrderInput : tkOrderInputList) {
			tkOrderInputService.insert(tkOrderInput);
		}
		return model;
	}
	
	// 京东报表数据下载后的入库
	@RequestMapping(value = "/reportJd2Db", method = RequestMethod.POST)
	@ResponseBody
	public Model reportJd2Db(Model model, HttpServletRequest request, HttpServletResponse response) {
		String gString = request.getParameter("gString");
//		System.out.println("reportJd2Db=="+gString);
		List<TkOrderInputJd> tkOrderInputJdList = GsonUtil.fromJsonList(gString, TkOrderInputJd.class);
		tkOrderInputJdService.truncateTkOrderInputJd();
		for (TkOrderInputJd tkOrderInputJd : tkOrderInputJdList) {
			tkOrderInputJdService.insert(tkOrderInputJd);
		}
		return model;
	}

	/**
	 * 根据位数生成验证码
	 * 
	 * @param size
	 *            位数
	 * @return
	 */
	public String getVcode(int size) {
		String retNum = "";
		// 定义验证码的范围
		// String codeStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		String codeStr = "1234567890";

		Random r = new Random();
		for (int i = 0; i < size; i++) {
			retNum += codeStr.charAt(r.nextInt(codeStr.length()));
		}
		return retNum;
	}
}
