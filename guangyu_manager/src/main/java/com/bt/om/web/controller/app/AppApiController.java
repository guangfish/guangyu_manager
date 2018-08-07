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
import com.bt.om.util.TaobaoUtil;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.app.task.Queue;
import com.bt.om.web.controller.app.task.TaskControl;
import com.bt.om.web.controller.app.task.WebQueue;
import com.bt.om.web.controller.app.task.WebTaskControl;
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
		// APP爬虫的逻辑
		if ("1".equals(appCrawlSwitch)) {
			productInfoVo = appCrawlLogic(userId, productUrl, tklSymbolsStr, pageNo, size);
		}
		// PC端爬虫逻辑
		else if ("2".equals(appCrawlSwitch)) {
			productInfoVo = webCrawlLogic(userId, productUrl, tklSymbolsStr, pageNo, size);
		}
		// 仅标题、关键词API查询的逻辑
		else if ("3".equals(appCrawlSwitch)) {
			productInfoVo = apiLogic(productUrl, pageNo, size);
		}
		// 启动网页爬虫、手机爬虫混合逻辑
		else if ("4".equals(appCrawlSwitch)) {
			// //随机任务分配模式
			// int randomInt = NumberUtil.getRandomInt(0, 1);
			// if(randomInt==0){
			// logger.info("执行APP爬虫逻辑");
			// productInfoVo = appCrawlLogic(userId, productUrl, tklSymbolsStr,
			// pageNo, size);
			// }else{
			// logger.info("执行WEB爬虫逻辑");
			// productInfoVo = webCrawlLogic(userId, productUrl, tklSymbolsStr,
			// pageNo, size);
			// }

			// 按队列大小分配模式，手机爬虫优先，当手机爬虫队列超过阈值时就走web爬虫
			String queueLengthControl = GlobalVariable.resourceMap.get("queue_length_control");
			int queueLength = Integer.parseInt(queueLengthControl);
			if (Queue.getSize() >= queueLength) {
				logger.info("APP爬从队列尺寸大于" + queueLength + "，执行WEB爬虫逻辑");
				productInfoVo = webCrawlLogic(userId, productUrl, tklSymbolsStr, pageNo, size);
			} else {
				logger.info("APP爬从队列尺寸小于" + queueLength + "，执行APP爬虫逻辑");
				productInfoVo = appCrawlLogic(userId, productUrl, tklSymbolsStr, pageNo, size);
			}
		}
		
		if(productInfoVo==null){
			productInfoVo=new ProductInfoVo();
			productInfoVo.setDesc("未查到商品信息");
			productInfoVo.setStatus("10");
			productInfoVo.setData(new ItemVo());
		}

		response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");

		model.addAttribute("response", productInfoVo);
		return model;
	}

	// 手机爬从的逻辑
	private ProductInfoVo appCrawlLogic(String userId, String productUrl, String tklSymbolsStr, int pageNo, int size) {
		ProductInfoVo productInfoVo = null;
		try {
			// 是淘口令请求时的逻辑
			if (TaobaoUtil.ifTkl(productUrl, tklSymbolsStr)) {
				logger.info("用户发送的是淘口令请求");
				//解析淘口令
				JsonObject tklObject = TaoKouling.parserTklObj(productUrl);
				String productUrlRedis = null;
				Object productUrlRedisObj = jedisPool.getFromCache("", productUrl.hashCode());
				if (productUrlRedisObj != null) {
					productUrlRedis = productUrlRedisObj.toString();
				}
				// 如果redis里有搜索过的商品名称，则直接通过API获取数据
				if (productUrlRedis != null) {
					productInfoVo = productInfoApi(productUrlRedis, pageNo, size);
				} else {
//					// 用正则去匹配标题，可能会匹配错误
//					String productNameRegex = GlobalVariable.resourceMap.get("productName_regex");
//					// 【(.*?)】http"
//					List<String[]> lists = RegexUtil.getListMatcher(productUrl, productNameRegex);
					
					String productUrlTmp = productUrl;
					String productTitle = tklObject!=null?tklObject.get("content").getAsString():"";
					String productTitleTmp = productTitle;
					long queueSize = Queue.getSize();

					// 队列长度大于3的话，直接走api接口
					String queueLengthControl = GlobalVariable.resourceMap.get("queue_length_control");
					int queueLength = Integer.parseInt(queueLengthControl);
					if (queueSize >= queueLength) {
						logger.info("APP爬虫队列长度=" + queueSize + ",走API接口");
						if (tklObject!=null) {
							// 根据淘口令搜索不到数据或无结果返回时，用商品名称通过API搜索，同时把商品名称放到redis中，在翻页搜索时起作用，就不用重复爬虫方式了
							jedisPool.putInCache("", productUrl.hashCode(), productTitleTmp, 120);
							// 特殊淘口令链接的处理，根据不同情况这里增加其他逻辑
							if (productTitleTmp.contains("这个#手聚App团购#宝贝不错")) {
								try {
									// 【这个#手聚App团购#宝贝不错:飞歌新品GS1大众迈腾雷凌卡罗拉英朗大屏导航一体智能车机(分享自@手机淘宝android客户端)】http://m.tb.cn/h.32A9Sl2
									// 点击链接，再选择浏览器咑閞；或復·制这段描述€GpKqb0uYtSj€后到淘♂寳♀
									productTitleTmp = productTitleTmp.substring(productTitleTmp.indexOf(":") + 1,
											productTitleTmp.lastIndexOf("("));
								} catch (Exception e) {
									logger.info("特殊标题分析错误，标题为【" + productTitleTmp + "】");
									e.printStackTrace();
								}
							}
							productInfoVo = productInfoApi(productTitleTmp, pageNo, size);
						}
					} else {
						// 启动线程，提前通过API获取数据，若爬虫爬不到数据则直接用接口返回值替换
						new Thread(new Runnable() {
							@Override
							public void run() {
								logger.info("启动线程，通过API获取商品数据");
								ProductInfoVo productInfoVoApi = null;
								String ptitle = productTitle;
								// 特殊淘口令链接的处理
								if (productTitle.contains("这个#手聚App团购#宝贝不错")) {
									try {
										// 【这个#手聚App团购#宝贝不错:飞歌新品GS1大众迈腾雷凌卡罗拉英朗大屏导航一体智能车机(分享自@手机淘宝android客户端)】http://m.tb.cn/h.32A9Sl2
										// 点击链接，再选择浏览器咑閞；或復·制这段描述€GpKqb0uYtSj€后到淘♂寳♀
										ptitle = productTitle.substring(productTitle.indexOf(":") + 1,
												productTitle.lastIndexOf("("));
									} catch (Exception e) {
										logger.info("特殊标题分析错误，标题为【" + productTitle + "】");
										e.printStackTrace();
									}
								}
								productInfoVoApi = productInfoApi(ptitle, 1, 30);
								jedisPool.putInCache("obj", productUrlTmp.hashCode(), productInfoVoApi, 60);
							}
						}).start();

						productInfoVo = productInfoAppCrawl(userId, productUrl,tklObject);
						if (productInfoVo.getData().getItems() == null) {
							if (tklObject!=null) {
								// 根据淘口令搜索不到数据或无结果返回时，用商品名称通过API搜索，同时把商品名称放到redis中，在翻页搜索时起作用，就不用重复爬虫方式了
								jedisPool.putInCache("", productUrl.hashCode(), productTitle, 120);
								// 从redis中获取提前通过线程获得的结果
								productInfoVo = (ProductInfoVo) (jedisPool.getFromCache("obj", productUrl.hashCode()));

							}
						}
					}
				}
			}
			// 请求为URL时的逻辑
			else if (TaobaoUtil.keyParser(productUrl, tklSymbolsStr)) {
				// 走网页爬虫逻辑，APP爬虫不支持按url搜索
				productInfoVo = productInfoWebCrawl(userId, productUrl,null);
//				// 爬不到数据就走api接口
//				if (productInfoVo.getData() == null) {
//					String productNameRegex = GlobalVariable.resourceMap.get("productName_regex");
//					List<String[]> lists = RegexUtil.getListMatcher(productUrl, productNameRegex);
//					if (lists.size() > 0) {
//						productInfoVo = productInfoApi((lists.get(0))[0], pageNo, size);
//					}
//				}
			}
			// 请求为标题或关键词的逻辑
			else {
				productInfoVo = productInfoApi(productUrl, pageNo, size);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return productInfoVo;
	}

	// 网页爬虫的逻辑
	private ProductInfoVo webCrawlLogic(String userId, String productUrl, String tklSymbolsStr, int pageNo, int size) {
		ProductInfoVo productInfoVo = null;
		try {
			// 淘口令请求
			if (TaobaoUtil.ifTkl(productUrl, tklSymbolsStr)) {
				//解析淘口令
				JsonObject tklObject = TaoKouling.parserTklObj(productUrl);
				String productUrlRedis = null;
				Object productUrlRedisObj = jedisPool.getFromCache("", productUrl.hashCode());
				if (productUrlRedisObj != null) {
					productUrlRedis = productUrlRedisObj.toString();
				}

				// 如果redis里有搜索过的商品名称，则直接通过API获取数据
				if (StringUtil.isNotEmpty(productUrlRedis)) {
					productInfoVo = productInfoApi(productUrlRedis, pageNo, size);
				} else {
//					// 用正则去匹配标题，可能会匹配错误
//					String productNameRegex = GlobalVariable.resourceMap.get("productName_regex");
//					List<String[]> lists = RegexUtil.getListMatcher(productUrl, productNameRegex);
					String productTitle = "";
					if(tklObject!=null){
						productTitle = tklObject.get("content").getAsString();
						System.out.println("淘口令解析后的标题="+productTitle);
					}
					long queueSize = WebQueue.getSize();
					String queueLengthControl = GlobalVariable.resourceMap.get("queue_length_control");
					int queueLength = Integer.parseInt(queueLengthControl);
					// 队列长度操作预设阈值时，就走API接口，网页爬虫比APP爬虫速度快，这里阈值再加1
					if (queueSize >= queueLength + 1) {
						logger.info("WEB爬虫队列长度=" + queueSize + ",走API接口");
						if (tklObject!=null) {
							// 根据淘口令搜索不到数据或无结果返回时，用商品名称通过API搜索，同时把商品名称放到redis中，在翻页搜索时起作用，就不用重复爬虫方式了
							jedisPool.putInCache("", productUrl.hashCode(), productTitle, 120);

							// 特殊淘口令链接的处理
							if (productTitle.contains("这个#手聚App团购#宝贝不错")) {
								try {
									// 【这个#手聚App团购#宝贝不错:飞歌新品GS1大众迈腾雷凌卡罗拉英朗大屏导航一体智能车机(分享自@手机淘宝android客户端)】http://m.tb.cn/h.32A9Sl2
									// 点击链接，再选择浏览器咑閞；或復·制这段描述€GpKqb0uYtSj€后到淘♂寳♀
									productTitle = productTitle.substring(productTitle.indexOf(":") + 1,
											productTitle.lastIndexOf("("));
								} catch (Exception e) {
									logger.info("特殊标题分析错误，标题为【" + productTitle + "】");
									e.printStackTrace();
								}
							}
							productInfoVo = productInfoApi(productTitle, pageNo, size);
						}
					} else {
						productInfoVo = productInfoWebCrawl(userId, productUrl,tklObject);
						if (productInfoVo.getData().getItems() == null) {
							logger.info("PC爬不到数据，走API接口==="+productTitle);
							if (tklObject!=null) {
								logger.info("1");
								// 根据淘口令搜索不到数据或无结果返回时，用商品名称通过API搜索，同时把商品名称放到redis中，在翻页搜索时起作用，就不用重复爬虫方式了
								jedisPool.putInCache("", productUrl.hashCode(), productTitle, 120);

								// 特殊淘口令链接的处理
								if (productTitle.contains("这个#手聚App团购#宝贝不错")) {
									try {
										// 【这个#手聚App团购#宝贝不错:飞歌新品GS1大众迈腾雷凌卡罗拉英朗大屏导航一体智能车机(分享自@手机淘宝android客户端)】http://m.tb.cn/h.32A9Sl2
										// 点击链接，再选择浏览器咑閞；或復·制这段描述€GpKqb0uYtSj€后到淘♂寳♀
										productTitle = productTitle.substring(productTitle.indexOf(":") + 1,
												productTitle.lastIndexOf("("));
									} catch (Exception e) {
										logger.info("特殊标题分析错误，标题为【" + productTitle + "】");
										e.printStackTrace();
									}
								}
								productInfoVo = productInfoApi(productTitle, pageNo, size);
							}
						}
					}
				}
			}
			// 请求为URL时的逻辑
			else if (TaobaoUtil.keyParser(productUrl, tklSymbolsStr)) {
				productInfoVo = productInfoWebCrawl(userId, productUrl,null);
//				if (productInfoVo.getData() == null) {
//					String productNameRegex = GlobalVariable.resourceMap.get("productName_regex");
//					List<String[]> lists = RegexUtil.getListMatcher(productUrl, productNameRegex);
//					if (lists.size() > 0) {
//						productInfoVo = productInfoApi((lists.get(0))[0], pageNo, size);
//					}
//				}
			} else {
				productInfoVo = productInfoApi(productUrl, pageNo, size);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return productInfoVo;
	}

	// api接口的逻辑
	private ProductInfoVo apiLogic(String productUrl, int pageNo, int size) {
		ProductInfoVo productInfoVo = null;
		productInfoVo = productInfoApi(productUrl, pageNo, size);
		return productInfoVo;
	}

	// APP爬虫任务
	public ProductInfoVo productInfoAppCrawl(String userId, String tklStr,JsonObject tklObject) {
		ProductInfoVo productInfoVo = new ProductInfoVo();
		try {
			String productUrl="";
			String imgUrl="";
			if(tklObject!=null){
				productUrl=tklObject.get("url").getAsString();
				imgUrl=tklObject.get("thumb_pic_url").getAsString();
			}else{
				productInfoVo.setStatus("11");
				productInfoVo.setDesc("淘口令解析失败");
				productInfoVo.setData(new ItemVo());
				return productInfoVo;
			}

			// 接口返回的图片地址可能是没有http前缀
			if (!imgUrl.contains("http:")) {
				imgUrl = "http:" + imgUrl;
			}

			logger.info("淘口令解析返回的图片地址【" + imgUrl + "】");

			// 需转换的淘口令，即用户提交上来的淘口令
			String tklOld = "";
			String tklSymbolsStr = GlobalVariable.resourceMap.get("tkl.symbol");
			List<String[]> lists = null;
			for (String symbol : tklSymbolsStr.split(";")) {
				String reg = symbol + ".*" + symbol;
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(tklStr);
				if (matcher.find()) {
					lists = RegexUtil.getListMatcher(tklStr, symbol + "(.*?)" + symbol);
					if (lists.size() > 0) {
						tklOld = symbol + (lists.get(0))[0] + symbol;
					}
					break;
				}
			}

			// logger.info("淘口令解析返回的图片地址保存到redis Key为=" + tklOld.hashCode());
			// 把通过淘口令解析返回的图片暂时放到redis中，等爬虫任务返回时关联图片
			if (StringUtil.isNotEmpty(imgUrl)) {
				jedisPool.putInCache("", tklOld.hashCode(), imgUrl, 60);
			}

			Map<String, String> urlMap0 = StringUtil.urlSplit(productUrl);
			String puri = urlMap0.get("puri");
			String productId = "";
			try {
				productId = puri.substring(puri.lastIndexOf("/") + 2, puri.lastIndexOf("."));
			} catch (Exception e) {
				e.printStackTrace();
			}

			String platform = "taobao";
			Map<String, String> map = new HashMap<>();
			ProductInfo productInfo = new ProductInfo();

			TaskControl taskControl = new TaskControl();
			Map<String, String> resultMap = taskControl.getProduct(tklOld);

			if (resultMap != null) {
				productInfo.setProductId(productId);
				productInfo.setProductInfoUrl(productUrl);
				String productImgUrl = resultMap.get("imgUrl");
				productInfo.setProductImgUrl(productImgUrl);
				String shopName = resultMap.get("shopName");
				productInfo.setShopName(shopName);
				String productName = resultMap.get("productName");
				productInfo.setProductName(productName);
				String tkLink = resultMap.get("tkUrl");
				productInfo.setTkLink(tkLink);
				double price = Double.valueOf(resultMap.get("price"));
				productInfo.setPrice(price);
				float incomeRate = Float.valueOf(resultMap.get("rate")) * 100;
				productInfo.setIncomeRate(incomeRate);
				float commission = Float.valueOf(resultMap.get("commission"));
				productInfo.setCommission(commission);
				String couponLink = resultMap.get("quanUrl");
				productInfo.setCouponLink(couponLink);
				productInfo.setCouponPromoLink(couponLink);
				String sellNum = resultMap.get("sellNum");
				productInfo.setMonthSales(Integer.parseInt(sellNum));
				String tkl = resultMap.get("tkl");
				productInfo.setTkl(tkl);
				String tklquan = resultMap.get("tklquan");
				productInfo.setTklquan(tklquan);
				String quanMianzhi = resultMap.get("quanMianzhi");
				productInfo.setCouponQuan(quanMianzhi);
				productInfo.setIfvalid(2);
				productInfo.setSourcefrom(2);
				productInfo.setCreateTime(new Date());
				productInfo.setUpdateTime(new Date());

				// 查询的商品信息入库
				try {
					productInfoService.insertProductInfo(productInfo);
				} catch (Exception e) {
					// logger.info(productInfo.getProductId()+"已入库");
					// logger.error(e.getMessage());
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
				productInfoVo.setData(new ItemVo());
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		return productInfoVo;
	}

	// 浏览器爬虫任务
	public ProductInfoVo productInfoWebCrawl(String userId, String productUrl,JsonObject tklObject) {
		ProductInfoVo productInfoVo = new ProductInfoVo();
		try {
			// 判断productUrl是否为淘口令，如果是淘口令通过接口获取商品链接
			String tklSymbolsStr = GlobalVariable.resourceMap.get("tkl.symbol");
			String[] tklSymbols = tklSymbolsStr.split(";");
			for (String symbol : tklSymbols) {
				String reg0 = symbol + ".*" + symbol;
				Pattern pattern0 = Pattern.compile(reg0);
				Matcher matcher0 = pattern0.matcher(productUrl);
				if (matcher0.find()) {
					if(tklObject!=null){
						productUrl=tklObject.get("url").getAsString();
					}else{
						productUrl=null;
					}
//					productUrl = TaoKouling.parserTkl(productUrl);
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
			// CrawlTask crawlTask = new CrawlTask();
			WebTaskControl webTaskControl = new WebTaskControl();
			Map<String, String> resultMap = null;
			// 如果是淘宝搜索的参数是商品地址
			if ("taobao".equals(platform)) {
				resultMap = webTaskControl.getProduct(productUrl);
			}
			// 如果是京东，搜索的参数是链接中商品ID
			else if ("jd".equals(platform)) {
				resultMap = webTaskControl.getProduct(uriProductId);
			}

			if (resultMap != null) {
				String productId = "";
				String productInfoUrl = "";
				if ("taobao".equals(platform)) {
					productId = urlMap.get("id");
					productInfoUrl = resultMap.get("productUrl");
				} else if ("jd".equals(platform)) {
					productId = uriProductId;
					productInfoUrl = urlMap.get("puri");
				} else {
					productId = "";
					productInfoUrl = "";
				}
				productInfo.setProductId(productId);
				productInfo.setProductInfoUrl(productInfoUrl);
				String productImgUrl = resultMap.get("imgUrl");
				productInfo.setProductImgUrl(productImgUrl);

				String shopName = resultMap.get("shopName");
				productInfo.setShopName(shopName);
				String productName = resultMap.get("productName");
				productInfo.setProductName(productName);
				String tkLink = resultMap.get("tkUrl");
				productInfo.setTkLink(tkLink);
				double price = Double.valueOf(resultMap.get("price").replace(",", ""));
				productInfo.setPrice(price);
				float incomeRate = Float.valueOf(resultMap.get("rate"));
				productInfo.setIncomeRate(incomeRate);
				float commission = Float.valueOf(resultMap.get("commission"));
				productInfo.setCommission(commission);
				String couponLink = resultMap.get("quanUrl");
				productInfo.setCouponLink(couponLink);
				productInfo.setCouponPromoLink(couponLink);
				String sellNum = resultMap.get("sellNum");
				productInfo.setMonthSales(Integer.parseInt(sellNum));
				String tkl = resultMap.get("tkl");
				productInfo.setTkl(tkl);
				String tklquan = resultMap.get("tklquan");
				productInfo.setTklquan(tklquan);
				String quanMianzhi = resultMap.get("quanMianzhi");
				productInfo.setCouponQuan(quanMianzhi);
				productInfo.setIfvalid(2);
				productInfo.setSourcefrom(2);
				productInfo.setCreateTime(new Date());
				productInfo.setUpdateTime(new Date());

				// 查询的商品信息入库
				try {
					productInfoService.insertProductInfo(productInfo);
				} catch (Exception e) {
					logger.info(productInfo.getProductId() + "已入库");
					// logger.error(e.getMessage());
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
				productInfoVo.setData(new ItemVo());
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
		} catch (Exception e) {
			e.printStackTrace();
		}

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

					String tklStr = TaoKouling.createTkl("https:" + tkurl,
							"【预估返:" + actualCommission + "】" + mapDataBean.getTitle(), mapDataBean.getPict_url());
					if (StringUtil.isNotEmpty(tklStr)) {
						TklResponse tklResponse = GsonUtil.GsonToBean(tklStr, TklResponse.class);
						map.put("tkl", tklResponse.getTbk_tpwd_create_response().getData().getModel());
					}

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

	// @RequestMapping(value = "/sendTask", method = { RequestMethod.POST,
	// RequestMethod.GET })
	// @ResponseBody
	// public Model sendTask(Model model, HttpServletRequest request,
	// HttpServletResponse response) {
	// String tkl = request.getParameter("url");
	// TaskControl taskControl = new TaskControl();
	// taskControl.sendTask(tkl);
	// return model;
	// }

}
