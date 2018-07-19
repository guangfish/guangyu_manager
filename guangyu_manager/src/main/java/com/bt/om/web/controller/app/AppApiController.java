package com.bt.om.web.controller.app;

import com.bt.om.cache.JedisPool;
import com.bt.om.entity.ProductInfo;
import com.bt.om.entity.SearchRecord;
import com.bt.om.service.IProductInfoService;
import com.bt.om.service.ISearchRecordService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.taobao.api.MapDataBean;
import com.bt.om.taobao.api.MaterialSearch;
import com.bt.om.taobao.api.MaterialSearchVo;
import com.bt.om.taobao.api.TaoKouling;
import com.bt.om.taobao.api.TklResponse;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.NumberUtil;
import com.bt.om.util.RegexUtil;
import com.bt.om.util.SecurityUtil1;
import com.bt.om.util.StringUtil;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.api.CrawlTask;
import com.bt.om.web.controller.api.TaskBean;
import com.bt.om.web.controller.app.vo.ItemVo;
import com.bt.om.web.controller.app.vo.ProductInfoVo;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/app/api")
public class AppApiController extends BasicController {
	private static final Logger logger = Logger.getLogger(AppApiController.class);
	@Autowired
	private JedisPool jedisPool;

	@Autowired
	private IProductInfoService productInfoService;

	@Autowired
	private ISearchRecordService searchRecordService;


	// // 获取商品详情
	// @RequestMapping(value = "/productInfo1", method = RequestMethod.POST)
	// @ResponseBody
	// public Model productInfo1(Model model, HttpServletRequest request,
	// HttpServletResponse response) {
	// ProductInfoVo productInfoVo = new ProductInfoVo();
	// productInfoVo.setStatus("0");
	// productInfoVo.setDesc("");
	// productInfoVo.setTotalSize(1);
	// productInfoVo.setCurPage(1);
	// productInfoVo.setMaxPage(1);
	// productInfoVo.setMall("taobao");
	// List<Map<String, String>> list = new ArrayList<>();
	// Map<String, String> map = new HashMap<>();
	// map.put("imgUrl",
	// "http://img.alicdn.com/bao/uploaded/i2/884679010/TB2IrJ3uFuWBuNjSszbXXcS7FXa_!!884679010.jpg");
	// map.put("shopName", "米吉诺拉づ外贸袜子日韩店");
	// map.put("productName", "米吉诺拉少女甜美生理内裤女士纯棉三角蕾丝性感月经期防漏姨妈裤");
	// map.put("commission", "10");
	// map.put("price", "100");
	// map.put("tkl", "€7OkK0BGAXeP€");
	// map.put("per", "20");
	// map.put("sellNum", "100");
	// map.put("quanMianzhi", "20");
	// map.put("fanliMultiple", "1.5");
	//
	// list.add(map);
	//
	// productInfoVo.setData(list);
	//
	// model.addAttribute("response", productInfoVo);
	// return model;
	// }

	// 获取商品详情
	@RequestMapping(value = "/productInfo", method = RequestMethod.POST)
	@ResponseBody
	public Model productInfo(Model model, HttpServletRequest request, HttpServletResponse response) {
		ProductInfoVo productInfoVo = new ProductInfoVo();
		String userId = "";
		String productUrl = "";
		int pageNo = 1;
		int size = 30;
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("userId") != null) {
				userId = obj.get("userId").getAsString();
				userId = SecurityUtil1.decrypts(userId);
			}
			if (obj.get("productUrl") != null) {
				productUrl = obj.get("productUrl").getAsString();
			}
			if (obj.get("pageNo") != null) {
				pageNo = obj.get("pageNo").getAsInt();
			}
			if (obj.get("size") != null) {
				size = obj.get("size").getAsInt();
			}
		} catch (IOException e) {
			productInfoVo.setStatus("1");
			productInfoVo.setDesc("系统繁忙，请稍后再试");
			model.addAttribute("response", productInfoVo);
			return model;
		}

		// 商品链接、淘口令非空验证
		if (StringUtils.isEmpty(productUrl)) {
			productInfoVo.setStatus("2");
			productInfoVo.setDesc("商品链接为空");
			model.addAttribute("response", productInfoVo);
			return model;
		}
		
		String tklSymbolsStr = GlobalVariable.resourceMap.get("tkl.symbol");
		String appCrawlSwitch = GlobalVariable.resourceMap.get("app_crawl_switch");
		if ("1".equals(appCrawlSwitch)) {
			if (ifTkl(productUrl, tklSymbolsStr)) {
				logger.info("淘口令请求");
				productInfoVo = productInfoAppCrawl(userId, productUrl);
				if (productInfoVo.getData() == null) {
					List<String[]> lists = RegexUtil.getListMatcher(productUrl, "【(.*?)】");
					if (lists.size() > 0) {
						productInfoVo = productInfoApi((lists.get(0))[0], pageNo, size);
					}
				}
			} else if (keyParser(productUrl, tklSymbolsStr)) {
				productInfoVo = productInfoWebCrawl(userId, productUrl);
				if (productInfoVo.getData() == null) {
					List<String[]> lists = RegexUtil.getListMatcher(productUrl, "【(.*?)】");
					if (lists.size() > 0) {
						productInfoVo = productInfoApi((lists.get(0))[0], pageNo, size);
					}
				}
			} else {
				productInfoVo = productInfoApi(productUrl, pageNo, size);
			}
		} else {
			if (keyParser(productUrl, tklSymbolsStr)) {
				productInfoVo = productInfoWebCrawl(userId, productUrl);
				if (productInfoVo.getData() == null) {
					List<String[]> lists = RegexUtil.getListMatcher(productUrl, "【(.*?)】");
					if (lists.size() > 0) {
						productInfoVo = productInfoApi((lists.get(0))[0], pageNo, size);
					}
				}
			} else {
				productInfoVo = productInfoApi(productUrl, pageNo, size);
			}
		}
		

		response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");

		model.addAttribute("response", productInfoVo);
		return model;
	}

	// APP爬虫任务
	public ProductInfoVo productInfoAppCrawl(String userId, String tklStr) {
		ProductInfoVo productInfoVo = new ProductInfoVo();
        try{
		String productUrl = TaoKouling.parserTklApp(tklStr);
		System.out.println(productUrl);
		String imgUrl=(productUrl.split(";;"))[1];
		productUrl=(productUrl.split(";;"))[0];
		System.out.println(productUrl);		
		if(!imgUrl.contains("http:")){
			imgUrl="http:"+imgUrl;
		}
		if(StringUtil.isNotEmpty(imgUrl)){
			ShardedJedis jedis = jedisPool.getResource();
			jedis.setex(tklStr.hashCode()+"", 3600, imgUrl);
			jedis.close();
		}
		Map<String, String> urlMap0 = StringUtil.urlSplit(productUrl);
		String puri = urlMap0.get("puri");
		String productId = puri.substring(puri.lastIndexOf("/") + 2, puri.lastIndexOf("."));

		String platform = "taobao";
		Map<String, String> map = new HashMap<>();
		ProductInfo productInfo = new ProductInfo();
		CrawlTask crawlTask = new CrawlTask();
		TaskBean taskBean = null;
		taskBean = crawlTask.getProduct(tklStr);
		if (StringUtils.isNotEmpty(taskBean.getMap().get("goodUrl1"))
				|| StringUtils.isNotEmpty(taskBean.getMap().get("goodUrl2"))) {
			String goodUrl = StringUtils.isEmpty(taskBean.getMap().get("goodUrl1")) ? taskBean.getMap().get("goodUrl2")
					: taskBean.getMap().get("goodUrl1");
			productInfo.setProductId(productId);
			productInfo.setProductInfoUrl(productUrl);
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
			String tkl = taskBean.getMap().get("tkl");
			productInfo.setTkl(tkl);
			String tklquan = taskBean.getMap().get("tklquan");
			productInfo.setTklquan(tklquan);
			String quanMianzhi = taskBean.getMap().get("quanMianzhi");
			productInfo.setCouponQuan(quanMianzhi);
			productInfo.setIfvalid(2);
			productInfo.setSourcefrom(2);
			productInfo.setCreateTime(new Date());
			productInfo.setUpdateTime(new Date());

			// 查询的商品信息入库
			try {
				productInfoService.insertProductInfo(productInfo);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

			// 插入搜索记录
			SearchRecord searchRecord = new SearchRecord();
			searchRecord.setMobile(userId);
			searchRecord.setProductId(productId);
			searchRecord.setMall(1);
			searchRecord.setStatus(1);
			searchRecord.setTitle(productName);
			searchRecord.setCreateTime(new Date());
			searchRecord.setUpdateTime(new Date());
			searchRecordService.insert(searchRecord);

			map.put("imgUrl", productImgUrl);
			map.put("shopName", shopName);
			map.put("productName", productName);
			map.put("price", "" + price);

			if (StringUtil.isNotEmpty(tklquan)) {
				map.put("tkl", tklquan);
			} else {
				map.put("tkl", tkl);
			}

			float pre = Float.parseFloat(NumberUtil.formatFloat(
					incomeRate * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")), "0.00"));
			map.put("per", pre + "");
			map.put("sellNum", sellNum);
			float actualCommission = (float) (Math
					.round(commission * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100))
					/ 100;
			if ("0.0".equals(quanMianzhi)) {
				quanMianzhi = "";
				actualCommission = commission * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate"));
			} else {
				actualCommission = (commission - Float.parseFloat(quanMianzhi) * incomeRate / 100)
						* Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate"));
			}
			actualCommission = Float.parseFloat(NumberUtil.formatFloat(actualCommission, "0.00"));
			map.put("quanMianzhi", quanMianzhi);
			map.put("commission", "" + actualCommission);

			float fanli = ((float) (Math
					.round(commission * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100))
					/ 100);
			float fanliMultiple = 1;

			if (fanli <= 1) {
				fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1"));
			} else if (fanli > 1 && fanli <= 5) {
				fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1-5"));
			} else if (fanli > 5 && fanli <= 10) {
				fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.5-10"));
			} else if (fanli > 10 && fanli <= 50) {
				fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.10-50"));
			} else if (fanli > 50 && fanli <= 100) {
				fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.50-100"));
			} else if (fanli > 100 && fanli <= 500) {
				fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.100-500"));
			} else {
				fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.500"));
			}
			map.put("fanliMultiple", fanliMultiple + "");
		} else {
			productInfoVo.setStatus("6");
			productInfoVo.setDesc("未查到商品信息");
			return productInfoVo;
		}

		List<Map<String, String>> list = new ArrayList<>();
		list.add(map);

		ItemVo itemVo = new ItemVo();

		// 查询成功
		productInfoVo.setStatus("0");
		productInfoVo.setDesc("查询成功");
		itemVo.setTotalSize(1);
		itemVo.setCurPage(1);
		itemVo.setMaxPage(1);
		itemVo.setMall(platform);
		itemVo.setHasNext(false);
		itemVo.setItems(list);
		productInfoVo.setData(itemVo);
        }catch(Exception e){
        	e.printStackTrace();;
        }

		return productInfoVo;
	}

	// 浏览器爬虫任务
	public ProductInfoVo productInfoWebCrawl(String userId, String productUrl) {
		ProductInfoVo productInfoVo = new ProductInfoVo();

		// 判断productUrl是否为淘口令，如果是淘口令通过接口获取商品链接
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
					return productInfoVo;
				} else {
					Map<String, String> urlMap0 = StringUtil.urlSplit(productUrl);
					String puri = urlMap0.get("puri");
					String pid = "";
					if (puri.contains("a.m.taobao.com")) {
						pid = puri.substring(puri.lastIndexOf("/") + 2, puri.lastIndexOf("."));
						productUrl = "https://item.taobao.com/item.htm" + "?id=" + pid;
					} else {
						productUrl = urlMap0.get("puri") + "?id=" + urlMap0.get("id");
					}
					logger.info("通过淘口令转换获得的商品缩短链接==>" + productUrl);
				}
				break;
			}
		}

		// 解析链接地址，并判断链接是否合法
		Map<String, String> urlMap = StringUtil.urlSplit(productUrl);
		String platform = "taobao";
		if (urlMap.get("puri").contains("taobao.com") || urlMap.get("puri").contains("tmall.com")) {
			platform = "taobao";
			productUrl = urlMap.get("puri") + "?id=" + urlMap.get("id");
		} else if (urlMap.get("puri").contains("jd.com")) {
			platform = "jd";
		} else {
			productInfoVo.setStatus("4");
			productInfoVo.setDesc("不支持的商品链接地址");
			return productInfoVo;
		}

		String uriProductId = "";
		if ("taobao".equals(platform)) {
			// 判断链接中是否有ID
			if (StringUtils.isEmpty(urlMap.get("id"))) {
				productInfoVo.setStatus("5");
				productInfoVo.setDesc("商品ID为空");
				return productInfoVo;
			}
			uriProductId = urlMap.get("id");
		} else if ("jd".equals(platform)) {
			String puri = urlMap.get("puri");
			// 截取京东商品ID
			String action = puri.substring(puri.lastIndexOf("/") + 1);
			if (action.contains(".")) {
				uriProductId = puri.substring(puri.lastIndexOf("/") + 1, puri.lastIndexOf("."));
			} else {
				uriProductId = action;
			}
			if (StringUtils.isEmpty(uriProductId)) {
				productInfoVo.setStatus("5");
				productInfoVo.setDesc("商品ID为空");
				return productInfoVo;
			}
		}

		Map<String, String> map = new HashMap<>();
		ProductInfo productInfo = new ProductInfo();
		CrawlTask crawlTask = new CrawlTask();
		TaskBean taskBean = null;
		// 如果是淘宝搜索的参数是商品地址
		if ("taobao".equals(platform)) {
			taskBean = crawlTask.getProduct(productUrl);
		}
		// 如果是京东，搜索的参数是链接中商品ID
		else if ("jd".equals(platform)) {
			taskBean = crawlTask.getProduct(uriProductId);
		}
		if (StringUtils.isNotEmpty(taskBean.getMap().get("goodUrl1"))
				|| StringUtils.isNotEmpty(taskBean.getMap().get("goodUrl2"))) {
			String goodUrl = StringUtils.isEmpty(taskBean.getMap().get("goodUrl1")) ? taskBean.getMap().get("goodUrl2")
					: taskBean.getMap().get("goodUrl1");

			String productId = "";
			String productInfoUrl = "";
			if ("taobao".equals(platform)) {
				productId = urlMap.get("id");
				productInfoUrl = taskBean.getMap().get("url");
			} else if ("jd".equals(platform)) {
				productId = uriProductId;
				productInfoUrl = urlMap.get("puri");
			} else {
				productId = "";
				productInfoUrl = "";
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
			String tkl = taskBean.getMap().get("tkl");
			productInfo.setTkl(tkl);
			String tklquan = taskBean.getMap().get("tklquan");
			productInfo.setTklquan(tklquan);
			String quanMianzhi = taskBean.getMap().get("quanMianzhi");
			productInfo.setCouponQuan(quanMianzhi);
			productInfo.setIfvalid(2);
			productInfo.setSourcefrom(2);
			productInfo.setCreateTime(new Date());
			productInfo.setUpdateTime(new Date());

			// 查询的商品信息入库
			try {
				productInfoService.insertProductInfo(productInfo);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

			// 插入搜索记录
			SearchRecord searchRecord = new SearchRecord();
			searchRecord.setMobile(userId);
			searchRecord.setProductId(productId);
			searchRecord.setMall(platform.equals("taobao") ? 1 : 2);
			searchRecord.setStatus(1);
			searchRecord.setTitle(productName);
			searchRecord.setCreateTime(new Date());
			searchRecord.setUpdateTime(new Date());
			searchRecordService.insert(searchRecord);

			map.put("imgUrl", productImgUrl);
			map.put("shopName", shopName);
			map.put("productName", productName);
			map.put("price", "" + price);
			if ("taobao".equals(platform)) {
				if (StringUtil.isNotEmpty(tklquan)) {
					map.put("tkl", tklquan);
				} else {
					map.put("tkl", tkl);
				}
			} else {
				map.put("tkl", tkLink);
			}

			float pre = Float.parseFloat(NumberUtil.formatFloat(
					incomeRate * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")), "0.00"));
			map.put("per", pre + "");
			map.put("sellNum", sellNum);
			float actualCommission = (float) (Math
					.round(commission * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100))
					/ 100;
			if ("0.0".equals(quanMianzhi)) {
				quanMianzhi = "";
				actualCommission = commission * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate"));
			} else {
				actualCommission = (commission - Float.parseFloat(quanMianzhi) * incomeRate / 100)
						* Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate"));
			}
			actualCommission = Float.parseFloat(NumberUtil.formatFloat(actualCommission, "0.00"));
			map.put("quanMianzhi", quanMianzhi);
			map.put("commission", "" + actualCommission);

			float fanli = ((float) (Math
					.round(commission * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100))
					/ 100);
			float fanliMultiple = 1;

			if (fanli <= 1) {
				fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1"));
			} else if (fanli > 1 && fanli <= 5) {
				fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1-5"));
			} else if (fanli > 5 && fanli <= 10) {
				fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.5-10"));
			} else if (fanli > 10 && fanli <= 50) {
				fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.10-50"));
			} else if (fanli > 50 && fanli <= 100) {
				fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.50-100"));
			} else if (fanli > 100 && fanli <= 500) {
				fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.100-500"));
			} else {
				fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.500"));
			}
			map.put("fanliMultiple", fanliMultiple + "");
		} else {
			productInfoVo.setStatus("6");
			productInfoVo.setDesc("未查到商品信息");
			return productInfoVo;
		}

		List<Map<String, String>> list = new ArrayList<>();
		list.add(map);

		ItemVo itemVo = new ItemVo();

		// 查询成功
		productInfoVo.setStatus("0");
		productInfoVo.setDesc("查询成功");
		itemVo.setTotalSize(1);
		itemVo.setCurPage(1);
		itemVo.setMaxPage(1);
		itemVo.setMall(platform);
		itemVo.setHasNext(false);
		itemVo.setItems(list);
		productInfoVo.setData(itemVo);

		return productInfoVo;
	}

	// 通过淘宝API查询商品信息
	public ProductInfoVo productInfoApi(String productUrl, int pageNo, int size) {
		ProductInfoVo productInfoVo = new ProductInfoVo();
		try {
			String retStr = MaterialSearch.materialSearch(productUrl, pageNo, size);
			MaterialSearchVo materialSearchVo = GsonUtil.GsonToBean(retStr, MaterialSearchVo.class);
			List<MapDataBean> mapDataBeanList = materialSearchVo.getTbk_dg_material_optional_response().getResult_list()
					.getMap_data();
			long total_results = materialSearchVo.getTbk_dg_material_optional_response().getTotal_results();
			List<Map<String, String>> list = new ArrayList<>();

			if (mapDataBeanList != null && mapDataBeanList.size() > 0) {
				String tkurl = "";
				for (MapDataBean mapDataBean : mapDataBeanList) {
					Map<String, String> map = new HashMap<>();
					map.put("imgUrl", mapDataBean.getPict_url());
					map.put("shopName", mapDataBean.getShop_title());
					map.put("productName", mapDataBean.getTitle());
					map.put("price", Float.parseFloat(mapDataBean.getZk_final_price()) + "");
					map.put("sellNum", mapDataBean.getVolume().intValue() + "");

					String quan = "";
					if (StringUtil.isNotEmpty(mapDataBean.getCoupon_info())) {
						Pattern p = Pattern.compile("减(\\d+)元");
						Matcher m = p.matcher(mapDataBean.getCoupon_info());
						if (m.find()) {
							quan = m.group(1);
							map.put("quanMianzhi", quan);
						}
						p = Pattern.compile("(\\d+)元无条件券");
						m = p.matcher(mapDataBean.getCoupon_info());
						if (m.find()) {
							quan = m.group(1);
							map.put("quanMianzhi", quan);
						}
						tkurl = mapDataBean.getCoupon_share_url();
					} else {
						tkurl = mapDataBean.getUrl();
					}
					String tklStr = TaoKouling.createTkl("https:" + tkurl, mapDataBean.getTitle(),
							mapDataBean.getPict_url());
					if (StringUtil.isNotEmpty(tklStr)) {
						TklResponse tklResponse = GsonUtil.GsonToBean(tklStr, TklResponse.class);
						map.put("tkl", tklResponse.getTbk_tpwd_create_response().getData().getModel());
					}
					float actualCommission = 0f;
					double actualPrice = 0d;
					double incomeRate = Double.parseDouble(mapDataBean.getCommission_rate()) / 100;
					if (StringUtil.isNotEmpty(quan)) {
						actualPrice = Double.parseDouble(mapDataBean.getZk_final_price()) - Double.parseDouble(quan);
					} else {
						actualPrice = Double.parseDouble(mapDataBean.getZk_final_price());
					}
					// actualCommission = ((float) (Math.round(actualPrice *
					// (incomeRate)
					// *
					// Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate"))
					// * 100)) / 100);
					actualCommission = ((float) (Math.round(actualPrice * (incomeRate)
							* Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100) / 100) / 100);
					map.put("commission", actualCommission + "");

					float pre = Float.parseFloat(NumberUtil.formatDouble(
							incomeRate * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")), "0.00"));
					map.put("per", pre + "");

					float fanliMultiple = 1;
					if (actualCommission <= 1) {
						fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1"));
					} else if (actualCommission > 1 && actualCommission <= 5) {
						fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1-5"));
					} else if (actualCommission > 5 && actualCommission <= 10) {
						fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.5-10"));
					} else if (actualCommission > 10 && actualCommission <= 50) {
						fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.10-50"));
					} else if (actualCommission > 50 && actualCommission <= 100) {
						fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.50-100"));
					} else if (actualCommission > 100 && actualCommission <= 500) {
						fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.100-500"));
					} else {
						fanliMultiple = Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.500"));
					}

					map.put("fanliMultiple", fanliMultiple + "");

					list.add(map);
				}

				ItemVo itemVo = new ItemVo();

				itemVo.setItems(list);
				itemVo.setMall("taobao");
				itemVo.setCurPage((int) pageNo);
				long maxPage = 0;
				boolean ifHasNextPage = false;
				if (total_results % size == 0) {
					maxPage = total_results / size;
				} else {
					maxPage = total_results / size + 1;
				}
				if (maxPage > pageNo) {
					ifHasNextPage = true;
				} else {
					ifHasNextPage = false;
				}
				itemVo.setMaxPage(maxPage);
				itemVo.setHasNext(ifHasNextPage);
				itemVo.setTotalSize(total_results);

				productInfoVo.setData(itemVo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productInfoVo;
	}

	/**
	 * 
	 * @param conetnt
	 *            搜索的字符串
	 * @param tklSymbolsStr
	 *            淘口令符号“€;￥;《”
	 * @return
	 */
	private boolean keyParser(String conetnt, String tklSymbolsStr) {
		if (conetnt.contains("jd.com") || conetnt.contains("taobao.com") || conetnt.contains("tmall.com")) {
			return true;
		} else {
			String[] tklSymbols = tklSymbolsStr.split(";");
			for (String symbol : tklSymbols) {
				String reg = symbol + ".*" + symbol;
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(conetnt);
				if (matcher.find()) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * 
	 * @param conetnt
	 * @param tklSymbolsStr
	 * @return
	 */
	private boolean ifTkl(String conetnt, String tklSymbolsStr) {
		String[] tklSymbols = tklSymbolsStr.split(";");
		for (String symbol : tklSymbols) {
			String reg = symbol + ".*" + symbol;
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(conetnt);
			if (matcher.find()) {
				return true;
			}
		}
		return false;
	}
}
