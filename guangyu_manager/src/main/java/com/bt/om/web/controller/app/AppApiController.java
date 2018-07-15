package com.bt.om.web.controller.app;

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
import com.bt.om.taobao.api.MapDataBean;
import com.bt.om.taobao.api.MaterialSearch;
import com.bt.om.taobao.api.MaterialSearchVo;
import com.bt.om.taobao.api.TaoKouling;
import com.bt.om.taobao.api.TklResponse;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.NumberUtil;
import com.bt.om.util.SecurityUtil1;
import com.bt.om.util.StringUtil;
import com.bt.om.vo.api.ProductCommissionVo;
import com.bt.om.vo.web.ResultVo;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.api.CrawlTask;
import com.bt.om.web.controller.api.TaskBean;
import com.bt.om.web.controller.app.vo.ItemVo;
import com.bt.om.web.controller.app.vo.ProductInfoVo;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/app/api")
public class AppApiController extends BasicController {
	private static final Logger logger = Logger.getLogger(AppApiController.class);

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

//	// 获取商品详情
//	@RequestMapping(value = "/productInfo1", method = RequestMethod.POST)
//	@ResponseBody
//	public Model productInfo1(Model model, HttpServletRequest request, HttpServletResponse response) {
//		ProductInfoVo productInfoVo = new ProductInfoVo();
//		productInfoVo.setStatus("0");
//		productInfoVo.setDesc("");
//		productInfoVo.setTotalSize(1);
//		productInfoVo.setCurPage(1);
//		productInfoVo.setMaxPage(1);
//		productInfoVo.setMall("taobao");
//		List<Map<String, String>> list = new ArrayList<>();
//		Map<String, String> map = new HashMap<>();
//		map.put("imgUrl",
//				"http://img.alicdn.com/bao/uploaded/i2/884679010/TB2IrJ3uFuWBuNjSszbXXcS7FXa_!!884679010.jpg");
//		map.put("shopName", "米吉诺拉づ外贸袜子日韩店");
//		map.put("productName", "米吉诺拉少女甜美生理内裤女士纯棉三角蕾丝性感月经期防漏姨妈裤");
//		map.put("commission", "10");
//		map.put("price", "100");
//		map.put("tkl", "€7OkK0BGAXeP€");
//		map.put("per", "20");
//		map.put("sellNum", "100");
//		map.put("quanMianzhi", "20");
//		map.put("fanliMultiple", "1.5");
//
//		list.add(map);
//
//		productInfoVo.setData(list);
//
//		model.addAttribute("response", productInfoVo);
//		return model;
//	}

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
		if (keyParser(productUrl, tklSymbolsStr)) {
			productInfoVo = productInfoCrawl(userId, productUrl);
		} else {
			productInfoVo = productInfoApi(productUrl, pageNo, size);
		}

		response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");

		model.addAttribute("response", productInfoVo);
		return model;
	}

	public ProductInfoVo productInfoCrawl(String userId, String productUrl) {
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

			// //查询的商品信息入库
			// try {
			// productInfoService.insertProductInfo(productInfo);
			// } catch (Exception e) {
			// logger.error(e.getMessage());
			// }

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
		
		ItemVo itemVo=new ItemVo();

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
						tkurl=mapDataBean.getCoupon_share_url();
					}else{
						tkurl=mapDataBean.getUrl();
					}
					String tklStr = TaoKouling.createTkl("https:"+tkurl, mapDataBean.getTitle(), mapDataBean.getPict_url());
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
//					actualCommission = ((float) (Math.round(actualPrice * (incomeRate)
//							* Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100)) / 100);
					actualCommission = ((float) (Math.round(actualPrice * (incomeRate)
							* Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100)/100) / 100);
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
				
				ItemVo itemVo=new ItemVo();
				
				
				itemVo.setItems(list);
				itemVo.setMall("taobao");
				itemVo.setCurPage((int) pageNo);
				long maxPage = 0;
				boolean ifHasNextPage=false;
				if (total_results % size == 0) {
					maxPage = total_results / size;
				} else {
					maxPage = total_results / size + 1;
				}
				if(maxPage>pageNo){
					ifHasNextPage=true;
				}else{
					ifHasNextPage=false;
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
						commissionList.add(((float) (Math.round(productInfo.getCommission()
								* Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100)) / 100));
						// commissionList.add(((float) (Math
						// .round(productInfo.getCommission() * 1 * 100))
						// / 100));
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
				map.put("quanMianzhi", "" + tkInfoTask.getQuanMianzhi());

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
		TkInfoTask tkInfoTask = null;
		Object object = ProductUrlTrans.get();
		if (object != null) {
			tkInfoTask = (TkInfoTask) object;
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
		String gString = request.getParameter("gString");
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
		// System.out.println("reportTb2Db=="+gString);
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
		// System.out.println("reportJd2Db=="+gString);
		List<TkOrderInputJd> tkOrderInputJdList = GsonUtil.fromJsonList(gString, TkOrderInputJd.class);
		tkOrderInputJdService.truncateTkOrderInputJd();
		for (TkOrderInputJd tkOrderInputJd : tkOrderInputJdList) {
			tkOrderInputJdService.insert(tkOrderInputJd);
		}
		return model;
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
}
