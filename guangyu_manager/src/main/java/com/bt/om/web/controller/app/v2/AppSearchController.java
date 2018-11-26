package com.bt.om.web.controller.app.v2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bt.om.cache.JedisPool;
import com.bt.om.entity.User;
import com.bt.om.service.IUserService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.taobao.api.MapDataBean;
import com.bt.om.taobao.api.MaterialSearch;
import com.bt.om.taobao.api.MaterialSearchVo;
import com.bt.om.taobao.api.SearchVo;
import com.bt.om.taobao.api.TaoKouling;
import com.bt.om.taobao.api.TklResponse;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.NumberUtil;
import com.bt.om.util.SecurityUtil1;
import com.bt.om.util.StringUtil;
import com.bt.om.web.controller.app.vo.ItemVo;
import com.bt.om.web.controller.app.vo.ProductInfoVo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController
@RequestMapping(value = "/app/api")
public class AppSearchController {
	private static final Logger logger = Logger.getLogger(AppSearchController.class);
	@Autowired
	private JedisPool jedisPool;
	@Autowired
	private IUserService userService;

	// 搜索页商品搜索接口
	@RequestMapping(value = "/productInfo", method = RequestMethod.POST)
	@ResponseBody
	public Model productInfo(Model model, HttpServletRequest request, HttpServletResponse response) {
		ProductInfoVo productInfoVo = new ProductInfoVo();
		model.addAttribute("response", productInfoVo);
		String version="";
		String app="";
		String userId = "";
		String productUrl = "";
		int pageNo = 1;
		int size = 20;
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("version") != null) {
				version = obj.get("version").getAsString();
			}
			if (obj.get("app") != null) {
				app = obj.get("app").getAsString();
			}
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
		} catch (IOException e) {
			productInfoVo.setStatus("1");
			productInfoVo.setDesc("系统繁忙，请稍后再试");
			productInfoVo.setData(new ItemVo());
			return model;
		}

		// 商品标题、淘口令非空验证
		if (StringUtils.isEmpty(productUrl)) {
			productInfoVo.setStatus("2");
			productInfoVo.setDesc("商品标题或淘口令为空");
			productInfoVo.setData(new ItemVo());
			return model;
		}

		// 通过淘宝API方式查询商品信息
		productInfoVo = apiLogic(userId, productUrl, pageNo, size);

		if (productInfoVo == null) {
			productInfoVo = new ProductInfoVo();
			productInfoVo.setDesc("未查到商品信息");
			productInfoVo.setStatus("10");
			productInfoVo.setData(new ItemVo());
		}

		model.addAttribute("response", productInfoVo);
		return model;
	}

	// api接口的逻辑
	private ProductInfoVo apiLogic(String userId, String productUrl, int pageNo, int size) {
		ProductInfoVo productInfoVo = null;
		productInfoVo = productInfoApi(userId, productUrl, pageNo, size);
		return productInfoVo;
	}

	// 通过淘宝API查询商品信息
	public ProductInfoVo productInfoApi(String userId, String productUrl, int pageNo, int size) {
		ProductInfoVo productInfoVo = null;
		String pid = "";
		if (StringUtil.isNotEmpty(userId)) {
			User user = userService.selectByMobile(userId);
			if (user != null) {
				if (StringUtil.isNotEmpty(user.getPid())) {
					pid = user.getPid();
				}
			}
		}
		try {
			// 用户在没有登陆状态下，默认广告位ID
			String defalutPid = ConfigUtil.getString("alimama.abigpush.default.pid", "176864894");
			SearchVo searchVo = new SearchVo();
			searchVo.setKey(productUrl);
			if (StringUtil.isEmpty(pid)) {
				pid = defalutPid;
			}
			searchVo.setPid(pid);
			searchVo.setPage(pageNo);
			searchVo.setSize(size);
			String retStr = MaterialSearch.materialSearch(searchVo);
			// logger.info(retStr);
			MaterialSearchVo materialSearchVo = GsonUtil.GsonToBean(retStr, MaterialSearchVo.class);
			List<MapDataBean> mapDataBeanList = materialSearchVo.getTbk_dg_material_optional_response().getResult_list()
					.getMap_data();
			long total_results = materialSearchVo.getTbk_dg_material_optional_response().getTotal_results();
			List<Map<String, String>> list = new ArrayList<>();

			if (mapDataBeanList != null && mapDataBeanList.size() > 0) {
				String tkurl = "";
				for (MapDataBean mapDataBean : mapDataBeanList) {
					Map<String, String> map = new HashMap<>();
					map.put("pid", pid);
					map.put("imgUrl", mapDataBean.getPict_url() + "_290x290.jpg");
					if (mapDataBean.getSmall_images() != null && mapDataBean.getSmall_images().getString().length > 0) {
						map.put("smallImgUrls", Arrays.toString(mapDataBean.getSmall_images().getString()));
					} else {
						map.put("smallImgUrls", "");
					}

					map.put("shopType", mapDataBean.getUser_type()+"");//卖家类型，0表示集市，1表示商城
					map.put("shopName", mapDataBean.getShop_title());
					map.put("productName", mapDataBean.getTitle());
					map.put("productShortName", mapDataBean.getShort_title());
					map.put("reservePrice", Float.parseFloat(mapDataBean.getReserve_price()) + "");// 原价
					map.put("price", Float.parseFloat(mapDataBean.getZk_final_price()) + "");// 折后价
					if (mapDataBean.getVolume() != null) {
						map.put("sellNum", mapDataBean.getVolume().intValue() + "");
					} else {
						map.put("sellNum", mapDataBean.getTk_total_sales());
					}

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

					actualCommission = ((float) (Math.round(actualPrice * (incomeRate)
							* Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100) / 100) / 100);
					map.put("commission", actualCommission + "");

					if (StringUtil.isEmpty(userId)) {
						map.put("showCommission", "no");
					} else {
						map.put("showCommission", "yes");
					}
					if (StringUtil.isEmpty(userId)) {
						map.put("showCoupon", "yes");
					} else {
						map.put("showCoupon", "yes");
					}

					if (!tkurl.startsWith("http")) {
						tkurl = "https:" + tkurl;
					}
					map.put("tkUrl", tkurl);

					map.put("title", mapDataBean.getTitle());
					map.put("pictUrl", mapDataBean.getPict_url());
					map.put("productId", mapDataBean.getNum_iid() + "");

					float pre = Float.parseFloat(NumberUtil.formatDouble(
							incomeRate * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")), "0.00"));
					map.put("per", pre + "");

					if ((int) actualCommission > 0) {
						list.add(map);
					}
				}

				BlockingQueue<Map<String, String>> queue = new LinkedBlockingQueue<>();
				for (Map<String, String> map : list) {
					queue.put(map);
				}

				// 启动固定线程数据模式
				for (int i = 0; i < 10; i++) {
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							Map<String, String> map = null;
							Object redisTklObj = null;
							String tklStr = "";
							while (true) {
								try {
									map = queue.remove();
									redisTklObj = jedisPool.getFromCache("tkl",
											map.get("pid") + "_" + map.get("productId"));
									if (redisTklObj != null) {
										System.out.println(map.get("pid") + "_" + map.get("productId") + "缓存命中了。。。");
										tklStr = (String) redisTklObj;
										map.put("tkl", tklStr);
									} else {
										tklStr = TaoKouling.createTkl(map.get("tkUrl"), map.get("title"),
												map.get("pictUrl"));
										if (StringUtil.isNotEmpty(tklStr)) {
											TklResponse tklResponse = GsonUtil.GsonToBean(tklStr, TklResponse.class);
											map.put("tkl",
													tklResponse.getTbk_tpwd_create_response().getData().getModel());
											jedisPool.putInCache("tkl", map.get("pid") + "_" + map.get("productId"),
													tklResponse.getTbk_tpwd_create_response().getData().getModel(),
													7 * 24 * 60 * 60);
										}
									}
								} catch (Exception e) {
									// e.printStackTrace();
									// 抛出异常代表线程结束
									break;
								}
							}
						}
					});
					thread.start();
					thread.join();
				}

				ItemVo itemVo = new ItemVo();
				itemVo.setItems(list);
				itemVo.setMall("taobao");
				itemVo.setIfJump("no");
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

				productInfoVo = new ProductInfoVo();
				productInfoVo.setData(itemVo);
			}
		} catch (Exception e) {
			logger.info("通过API接口查询不到商品，标题为==>" + productUrl);
			e.printStackTrace();
		}
		return productInfoVo;
	}

	@RequestMapping(value = "/getTkl", method = RequestMethod.POST)
	@ResponseBody
	public Model getTkl(Model model, HttpServletRequest request, HttpServletResponse response) {
		com.bt.om.web.controller.xcx.util.ProductInfoVo productInfoVo = new com.bt.om.web.controller.xcx.util.ProductInfoVo();
		String version ="";
		String app="";
		String userId = "";
		String productId = "";
		String tkUrl = "";
		String title = "";
		String imgUrl = "";
		String mobile = "";
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("version") != null) {
				version = obj.get("version").getAsString();
			}
			if (obj.get("app") != null) {
				app = obj.get("app").getAsString();
			}
			if (obj.get("userId") != null) {
				userId = obj.get("userId").getAsString();
				userId = SecurityUtil1.decrypts(userId);
				mobile = userId;
			}
			if (obj.get("productId") != null) {
				productId = obj.get("productId").getAsString();
			}
			if (obj.get("tkUrl") != null) {
				tkUrl = obj.get("tkUrl").getAsString();
			}
			if (obj.get("title") != null) {
				title = obj.get("title").getAsString();
			}
			if (obj.get("imgUrl") != null) {
				imgUrl = obj.get("imgUrl").getAsString();
			}
		} catch (IOException e) {
			productInfoVo.setStatus("1");
			productInfoVo.setDesc("系统繁忙，请稍后再试");
			productInfoVo.setData(new com.bt.om.web.controller.xcx.util.ItemVo());
			model.addAttribute("response", productInfoVo);
			return model;
		}

		String pid = "";
		if (StringUtil.isNotEmpty(userId)) {
			User user = userService.selectByMobile(userId);
			if (user != null) {
				if (StringUtil.isNotEmpty(user.getPid())) {
					pid = user.getPid();
				}
			}
		}

		if (StringUtil.isEmpty(pid)) {
			pid = ConfigUtil.getString("alimama.abigpush.default.pid", "176864894");
		}

		Object redisTklObj = jedisPool.getFromCache("tkl", pid + "_" + productId);
		String tkl = "";
		if (redisTklObj != null) {
			logger.info(productId + "淘口令缓存命中");
			tkl = (String) redisTklObj;
		} else {
			String tklStr = TaoKouling.createTkl(tkUrl, title, imgUrl);
			if (StringUtil.isNotEmpty(tklStr)) {
				TklResponse tklResponse = GsonUtil.GsonToBean(tklStr, TklResponse.class);
				tkl = tklResponse.getTbk_tpwd_create_response().getData().getModel();
				jedisPool.putInCache("tkl", pid + "_" + productId,
						tklResponse.getTbk_tpwd_create_response().getData().getModel(), 7 * 24 * 60 * 60);
			}
		}

		productInfoVo.setData(new com.bt.om.web.controller.xcx.util.ItemVo());
		productInfoVo.getData().setTkl(tkl);
		model.addAttribute("response", productInfoVo);

		return model;
	}
}
