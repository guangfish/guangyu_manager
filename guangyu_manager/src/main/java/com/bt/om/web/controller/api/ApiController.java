package com.bt.om.web.controller.api;

import com.bt.om.cache.JedisPool;
import com.bt.om.common.SysConst;
import com.bt.om.entity.ProductInfo;
import com.bt.om.entity.TkInfoTask;
import com.bt.om.enums.ResultCode;
import com.bt.om.enums.SessionKey;
import com.bt.om.selenium.ProductUrlTrans;
import com.bt.om.service.IProductInfoService;
import com.bt.om.service.ITkInfoTaskService;
import com.bt.om.taobao.api.TaoKouling;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.StringUtil;
import com.bt.om.util.TaobaoSmsUtil;
import com.bt.om.vo.api.GetSmsCodeVo;
import com.bt.om.vo.api.ProductCommissionVo;
import com.bt.om.vo.api.ProductInfoVo;
import com.bt.om.vo.api.UserOrderVo;
import com.bt.om.vo.web.ResultVo;
import com.bt.om.web.BasicController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
public class ApiController extends BasicController {
	private static final Logger logger = Logger.getLogger(ApiController.class);

	@Autowired
	private IProductInfoService productInfoService;

	@Autowired
	private ITkInfoTaskService tkInfoTaskService;

	// @Autowired
	// private JedisService jedisService;

	@Autowired
	private JedisPool jedisPool;

	// 获取验证码
	@RequestMapping(value = "/getSmsCode", method = RequestMethod.POST)
	@ResponseBody
	public Model getSmsCode(Model model, HttpServletRequest request, HttpServletResponse response) {
		ResultVo<GetSmsCodeVo> result = new ResultVo<>();
		result.setCode(ResultCode.RESULT_SUCCESS.getCode());
		result.setResultDes("获取验证码成功");
		model = new ExtendedModelMap();
		String mobile = null;
		String code = "";
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			mobile = obj.get("mobile").getAsString();
			code = obj.get("vcode").getAsString();
		} catch (IOException e) {
			result.setCode(ResultCode.RESULT_FAILURE.getCode());
			result.setResultDes("系统繁忙，请稍后再试！");
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}

		// 手机号验证
		if (StringUtils.isEmpty(mobile)) {
			result.setCode(ResultCode.RESULT_FAILURE.getCode());
			result.setResultDes("手机号为必填！");
			result.setResult(new GetSmsCodeVo("","1"));
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}
		// 验证码验证
		if (StringUtils.isEmpty(code)) {
			result.setCode(ResultCode.RESULT_FAILURE.getCode());
			result.setResultDes("验证码为必填！");
			result.setResult(new GetSmsCodeVo("","2"));
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}

		String sessionCode = request.getSession().getAttribute(SessionKey.SESSION_CODE.toString()) == null ? ""
				: request.getSession().getAttribute(SessionKey.SESSION_CODE.toString()).toString();

		// 验证码有效验证
		if (!code.equalsIgnoreCase(sessionCode)) {
			result.setCode(ResultCode.RESULT_FAILURE.getCode());
			result.setResultDes("验证码验证失败！");
			result.setResult(new GetSmsCodeVo("","3"));
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}
		
		if(jedisPool.getResource().exists(mobile)){
			result.setCode(ResultCode.RESULT_FAILURE.getCode());
			result.setResultDes("请等待2分钟后再次发送短信验证码！");
			result.setResult(new GetSmsCodeVo("","4"));
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}

		String vcode = getVcode(5);
		System.out.println(vcode);
		// jedisService.putInCache("gy", "vcode", vcode, 60);
		jedisPool.getResource().setex(mobile, 120, vcode);

		// 发送短信验证码
		if ("on".equals(ConfigUtil.getString("is.sms.send"))) {
			TaobaoSmsUtil.sendSms("逛鱼返利", "SMS_125955002","vcode", vcode, mobile);
		}

		// System.out.println(jedisPool.getResource().get("vcode"));
		result.setResult(new GetSmsCodeVo(vcode,"0"));
		model.addAttribute(SysConst.RESULT_KEY, result);
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");

		return model;
	}

	// 获取商品详情
	@RequestMapping(value = "/productInfo", method = RequestMethod.POST)
	@ResponseBody
	public Model productInfo(Model model, HttpServletRequest request, HttpServletResponse response) {
		String ua=request.getHeader("User-Agent");
		System.out.println(ua);
		String ifWeixinBrower="no";
		if((ua.toLowerCase()).contains("micromessenger")){
			ifWeixinBrower="yes";
		}
		ResultVo<ProductInfoVo> result = new ResultVo<>();
		result.setCode(ResultCode.RESULT_SUCCESS.getCode());
		result.setResultDes("商品信息获取成功");
		model = new ExtendedModelMap();
		@SuppressWarnings("unused")
		String user_id;
		String product_url = "";
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			user_id = obj.get("user_id").getAsString();

			product_url = obj.get("product_url").getAsString();
		} catch (IOException e) {
			result.setCode(ResultCode.RESULT_FAILURE.getCode());
			result.setResultDes("系统繁忙，请稍后再试！");
			result.setResult(new ProductInfoVo("","","","","1"));
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}

		// 商品链接验证
		if (StringUtils.isEmpty(product_url)) {
			result.setCode(ResultCode.RESULT_FAILURE.getCode());
			result.setResultDes("商品链接为空！");
			result.setResult(new ProductInfoVo("","","","","2"));
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}
		
		//判断是否发送的是淘口令请求
		String reg="￥.*￥";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(product_url);
		if(matcher.find()){
			product_url=TaoKouling.parserTkl(product_url);
			logger.info("通过淘口令转换获得的商品链接==>"+product_url);
			if(StringUtils.isEmpty(product_url)){
				result.setCode(ResultCode.RESULT_FAILURE.getCode());
				result.setResultDes("商品链接为空！");
				result.setResult(new ProductInfoVo("","","","","2"));
				model.addAttribute(SysConst.RESULT_KEY, result);
				return model;
			}else{
				Map<String, String> urlMap0 = StringUtil.urlSplit(product_url);
				product_url=urlMap0.get("puri")+"?id="+urlMap0.get("id");
				logger.info("通过淘口令转换获得的商品缩短链接==>"+product_url);
			}
		}

		Map<String, String> urlMap = StringUtil.urlSplit(product_url);
		String platform = "taobao";
		if (urlMap.get("puri").contains("taobao.com") || urlMap.get("puri").contains("tmall.com")) {
			platform = "taobao";
		} else if (urlMap.get("puri").contains("jd.com")) {
			platform = "jd";
		}else{
			result.setCode(ResultCode.RESULT_FAILURE.getCode());
			result.setResultDes("不支持的URL地址！");
			result.setResult(new ProductInfoVo("","","","","4"));
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}
        
		ProductInfo productInfo = null;
		String uriProductId="";
		if ("taobao".equals(platform)) {
			// 判断链接中是否有ID
			if (StringUtils.isEmpty(urlMap.get("id"))) {
				result.setCode(ResultCode.RESULT_FAILURE.getCode());
				result.setResultDes("商品ID为空！");
				result.setResult(new ProductInfoVo("","","","","3"));
				model.addAttribute(SysConst.RESULT_KEY, result);
				return model;
			}
			productInfo = productInfoService.getByProductId(urlMap.get("id"));
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
				result.setCode(ResultCode.RESULT_FAILURE.getCode());
				result.setResultDes("商品ID为空！");
				result.setResult(new ProductInfoVo("","","","","3"));
				model.addAttribute(SysConst.RESULT_KEY, result);
				return model;
			}			
			productInfo = productInfoService.getByProductId(uriProductId);
		}

		String msg = "";
		if (productInfo == null) {
			productInfo = new ProductInfo();
			CrawlTask crawlTask = new CrawlTask();
			TaskBean taskBean = null;
			//如果是淘宝搜索的参数是商品地址
			if("taobao".equals(platform)){
				taskBean = crawlTask.getProduct(product_url);
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
				String sellNum = taskBean.getMap().get("sellNum");
				productInfo.setMonthSales(Integer.parseInt(sellNum));
				String tkl=taskBean.getMap().get("tkl");
				productInfo.setTkl(tkl);
				String tklquan=taskBean.getMap().get("tklquan");
				productInfo.setTklquan(tklquan);
				productInfo.setCreateTime(new Date());
				productInfo.setUpdateTime(new Date());
				productInfoService.insertProductInfo(productInfo);

				// 组装msg
				StringBuffer sb = new StringBuffer();
				sb.append(
						"<div id='e-c' align=center></div><div style='font-size:12px;width:330px;top:10%;left:38%;background:#fff;border-radius:10px;box-shadow:5px 5px 10px #888;'><div><img src='");
				sb.append(productImgUrl);
				if ("no".equals(ifWeixinBrower)) {
					sb.append("' height='220' width='220' onclick=drump('");
					if (StringUtils.isNotEmpty(couponLink)) {
						sb.append(couponLink);
					} else {
						sb.append(tkLink);
					}
				}else{
					if("taobao".equals(platform)){
						sb.append("' height='220' width='220' id='copy' onclick=jsCopy('");
						if (StringUtils.isNotEmpty(tklquan)) {
							sb.append("tklquan");
						} else {
							sb.append("tkl");
						}
					}else{
						sb.append("' height='220' width='220' onclick=drump('");
						if (StringUtils.isNotEmpty(couponLink)) {
							sb.append(couponLink);
						} else {
							sb.append(tkLink);
						}
					}
				}
				if("yes".equals(ifWeixinBrower)){
					if("taobao".equals(platform)){
						sb.append("')></div><div style='color:red;'>△</div><div style='color:red;'>点击图片复制淘口令，然后打开手机淘宝购物</div><div>");
					}else{
						sb.append("')></div><div style='color:red;'>△</div><div style='color:red;'>点击图片返回京东购物。</div><div>");
					}
				}else{
				  sb.append("')></div><div style='color:red;'>△</div><div style='color:red;'>点击图片返回"+("taobao".equals(platform)?"淘宝":"京东")+"购物。</div><div>");
				}
				sb.append(productName);
				sb.append(
						"</div><div style='height:20px;'><span style='float:left;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;商店：");
				sb.append(shopName);
				sb.append("</span><span style='float:right;'>月销量：");
				sb.append(sellNum);
				sb.append(
						"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></div><div style='height: 20px;'><span style='float: left;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;价格：￥");
				sb.append(price);
				sb.append("</span><span style='float: right;'>预估返现：￥");
				sb.append(((float) (Math.round(commission * ConfigUtil.getFloat("commission.rate", 1) * 100)) / 100));
//				sb.append(((float) (Math.round(commission * 1 * 100)) / 100));
				sb.append("(");
				sb.append(((float) (Math.round(incomeRate * ConfigUtil.getFloat("commission.rate", 1) * 100)) / 100));
//				sb.append(((float) (Math.round(incomeRate * 1 * 100)) / 100));
				sb.append("%)");
				sb.append(
						"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></div><div id='btn-app'><input type='hidden' id='tkl' value='"+tkl+"'><input type='hidden' id='tklquan' value='"+tklquan+"'>");
				
				sb.append("</div></div></div>");
				msg = sb.toString();
			} else {
				return model;
			}
		} else {
			// 组装msg
			StringBuffer sb = new StringBuffer();
			sb.append(
					"<div id='e-c' align=center></div><div style='font-size:12px;width:330px;top:10%;left:38%;background:#fff;border-radius:10px;box-shadow:5px 5px 10px #888;'><div><img src='");
			sb.append(productInfo.getProductImgUrl());
			
			if ("no".equals(ifWeixinBrower)) {
				sb.append("' height='220' width='220' onclick=drump('");
				if (StringUtils.isNotEmpty(productInfo.getCouponLink())) {
					sb.append(productInfo.getCouponLink());
				} else {
					sb.append(productInfo.getTkLink());
				}
			}else{
				if("taobao".equals(platform)){
					sb.append("' height='220' width='220' id='copy' onclick=jsCopy('");
					if (StringUtils.isNotEmpty(productInfo.getTklquan())) {
						sb.append("tklquan");
					} else {
						sb.append("tkl");
					}
				}else{
					sb.append("' height='220' width='220' onclick=drump('");
					if (StringUtils.isNotEmpty(productInfo.getCouponLink())) {
						sb.append(productInfo.getCouponLink());
					} else {
						sb.append(productInfo.getTkLink());
					}
				}				
			}
			if("yes".equals(ifWeixinBrower)){
				if("taobao".equals(platform)){
					sb.append("')></div><div style='color:red;'>△</div><div style='color:red;'>点击图片复制淘口令，然后打开手机淘宝购物</div><div>");
				}else{
					sb.append("')></div><div style='color:red;'>△</div><div style='color:red;'>点击图片返回京东购物。</div><div>");
				}
			}else{
			  sb.append("')></div><div style='color:red;'>△</div><div style='color:red;'>点击图片返回"+("taobao".equals(platform)?"淘宝":"京东")+"购物。</div><div>");
			}
			sb.append(productInfo.getProductName());
			sb.append("</div><div style='height:20px;'><span style='float:left;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;商店：");
			sb.append(productInfo.getShopName());
			sb.append("</span><span style='float:right;'>月销量：");
			sb.append(productInfo.getMonthSales());
			sb.append(
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></div><div style='height: 20px;'><span style='float: left;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;价格：￥");
			sb.append(productInfo.getPrice());
			sb.append("</span><span style='float: right;'>预估返现：￥");
			sb.append(
					((float) (Math.round(productInfo.getCommission() * ConfigUtil.getFloat("commission.rate", 1) * 100))
							/ 100));
//			sb.append(
//					((float) (Math.round(productInfo.getCommission() * 1 * 100))
//							/ 100));
			sb.append("(");
			sb.append(((float) (Math.round(productInfo.getIncomeRate() * ConfigUtil.getFloat("commission.rate", 1) * 100)) / 100));
//			sb.append(((float) (Math.round(productInfo.getIncomeRate() * 1 * 100)) / 100));
			sb.append("%)");
			sb.append(
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></div><div id='btn-app'><input type='hidden' id='tkl' value='"+productInfo.getTkl()+"'><input type='hidden' id='tklquan' value='"+productInfo.getTklquan()+"'>");
			
			sb.append("</div></div></div>");
			msg = sb.toString();
		}
        //查询成功
		result.setResult(new ProductInfoVo(productInfo.getTkLink(), "领券", productInfo.getCouponLink(), msg,"0"));
		model.addAttribute(SysConst.RESULT_KEY, result);
		// response.getHeaders().add("Access-Control-Allow-Credentials","true");
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");

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
								.round(productInfo.getCommission() * ConfigUtil.getFloat("commission.rate", 1) * 100))
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
						"<div id='e-c' style='position:fixed;z-index:999999999;width:100%;height:100%;left:0;top:0;' align=center><div style='background:#000;width:100%;height:100%;opacity:0.2;'></div><div style='font-size:12px;width:330px;position:fixed;top:10%;left:38%;background:#fff;border-radius:10px;box-shadow:5px 5px 10px #888;'><h2 style='padding:5px;font-size:18px;'>该商品无佣金。</h2></div></div>");
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
