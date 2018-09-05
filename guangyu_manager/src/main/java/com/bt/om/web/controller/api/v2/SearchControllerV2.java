package com.bt.om.web.controller.api.v2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.cache.JedisPool;
import com.bt.om.entity.Banner;
import com.bt.om.entity.ProductInfo;
import com.bt.om.entity.SearchRecord;
import com.bt.om.service.IBannerService;
import com.bt.om.service.IProductInfoService;
import com.bt.om.service.ISearchRecordService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.taobao.api.MapDataBean;
import com.bt.om.taobao.api.MaterialSearch;
import com.bt.om.taobao.api.MaterialSearchVo;
import com.bt.om.taobao.api.TaoKouling;
import com.bt.om.taobao.api.TklResponse;
import com.bt.om.taobao.api.coupon.CouponBean;
import com.bt.om.taobao.api.coupon.CouponGet;
import com.bt.om.taobao.api.coupon.CouponSearchVo;
import com.bt.om.util.DateUtil;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.RequestUtil;
import com.bt.om.util.StringUtil;
import com.bt.om.vo.web.SearchDataVo;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.vo.JsonResult;
import com.bt.om.web.util.CookieHelper;
import com.bt.om.web.util.SearchUtil;

import redis.clients.jedis.ShardedJedis;

/**
 * 逛鱼搜索Controller
 */
@Controller
@RequestMapping(value = "/v2")
public class SearchControllerV2 extends BasicController {
	@Autowired
	IProductInfoService productInfoService;

	@Autowired
	private IBannerService bannerService;

	@Autowired
	private ISearchRecordService searchRecordService;

	@Autowired
	private JedisPool jedisPool;

	@RequestMapping(value = "/search", method = { RequestMethod.GET, RequestMethod.POST })
	public String search(Model model, HttpServletRequest request) {
		String ua = request.getHeader("User-Agent");
		String ifWeixinBrower = "no";
		if ((ua.toLowerCase()).contains("micromessenger")) {
			ifWeixinBrower = "yes";
		}
		model.addAttribute("ifWeixinBrower", ifWeixinBrower);

		List<Banner> bannerList = bannerService.selectAll(1);
		model.addAttribute("bannerList", bannerList);

		List<Banner> campaignList = bannerService.selectCampaign(5);
		model.addAttribute("campaignList", campaignList);

		float rate = Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate"));
		model.addAttribute("rate", rate);

		List<ProductInfo> productInfoList = new ArrayList<>();

		Object productInfoListObj = jedisPool.getFromCache("v2", "productInfoList");
		if (productInfoListObj != null) {
			productInfoList = (List<ProductInfo>) productInfoListObj;
		} else {
			// 通过接口取优惠群数据
			CouponSearchVo couponSearchVo = GsonUtil.GsonToBean(CouponGet.couponGet(null, 1, 30), CouponSearchVo.class);
			List<CouponBean> couponBeanList = couponSearchVo.getTbk_dg_item_coupon_get_response().getResults()
					.getTbk_coupon();
			long total_results = couponSearchVo.getTbk_dg_item_coupon_get_response().getTotal_results();
			System.out.println(total_results);

			if (couponBeanList != null && couponBeanList.size() > 0) {
				for (CouponBean couponBean : couponBeanList) {
					String tkurl = "";
					ProductInfo productInfo = new ProductInfo();
					productInfo.setProductName(couponBean.getTitle());
					productInfo.setProductImgUrl(couponBean.getPict_url());
					productInfo.setShopName(couponBean.getShop_title());
					productInfo.setPrice(Double.parseDouble(couponBean.getZk_final_price()));
					productInfo.setZkPrice(Float.parseFloat(couponBean.getZk_final_price()));
					productInfo.setMonthSales(couponBean.getVolume().intValue());
					productInfo.setProductId(couponBean.getNum_iid().toString());
					productInfo.setPlatformType(couponBean.getUser_type() == 0 ? "淘宝" : "天猫");
					String quan = "";
					if (StringUtil.isNotEmpty(couponBean.getCoupon_info())) {
						productInfo.setCouponMiane(couponBean.getCoupon_info());
						productInfo.setCouponRest(couponBean.getCoupon_remain_count().intValue());
						Pattern p = Pattern.compile("减(\\d+)元");
						Matcher m = p.matcher(couponBean.getCoupon_info());
						if (m.find()) {
							quan = m.group(1);
							productInfo.setCouponQuan(quan);
						}
						p = Pattern.compile("(\\d+)元无条件券");
						m = p.matcher(couponBean.getCoupon_info());
						if (m.find()) {
							quan = m.group(1);
							productInfo.setCouponQuan(quan);
						}
						tkurl = couponBean.getCoupon_click_url();
						productInfo.setCouponPromoLink(tkurl);
					}
					String tklStr = TaoKouling.createTkl(tkurl, couponBean.getTitle(), couponBean.getPict_url());
					if (StringUtil.isNotEmpty(tklStr)) {
						TklResponse tklResponse = GsonUtil.GsonToBean(tklStr, TklResponse.class);
						productInfo.setTkl(tklResponse.getTbk_tpwd_create_response().getData().getModel());
					}
					float commission = 0f;
					float actualCommission = 0f;
					double actualPrice = 0d;
					double incomeRate = Double.parseDouble(couponBean.getCommission_rate()) / 100;
					if (StringUtil.isNotEmpty(quan)) {
						actualPrice = Double.parseDouble(couponBean.getZk_final_price()) - Double.parseDouble(quan);
					} else {
						actualPrice = Double.parseDouble(couponBean.getZk_final_price());
					}
					commission = ((float) (Math.round(actualPrice * (incomeRate) * 100)) / 100);
					actualCommission = ((float) (Math.round(actualPrice * (incomeRate)
							* Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100)) / 100);

					// System.out.println(actualPrice+"-"+couponBean.getCommission_rate()+"-"+incomeRate+"-"+actualCommission);

					productInfo.setCommission(commission);
					productInfo.setActualCommission(actualCommission);

					if (actualCommission <= 1) {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1")));
					} else if (actualCommission > 1 && actualCommission <= 5) {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1-5")));
					} else if (actualCommission > 5 && actualCommission <= 10) {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.5-10")));
					} else if (actualCommission > 10 && actualCommission <= 50) {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.10-50")));
					} else if (actualCommission > 50 && actualCommission <= 100) {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.50-100")));
					} else if (actualCommission > 100 && actualCommission <= 500) {
						productInfo
								.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.100-500")));
					} else {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.500")));
					}

					productInfoList.add(productInfo);
				}
				jedisPool.putInCache("v2", "productInfoList", productInfoList, 60*60);
			}
		}

		// 通过数据库取优惠券数据
		// List<ProductInfo> productInfoList =
		// productInfoService.selectProductInfoListRand(30);
		// for (ProductInfo productInfo : productInfoList) {
		// double commission = productInfo.getCommission();
		// commission = productInfo.getPrice() -
		// Double.parseDouble(productInfo.getCouponQuan());
		// productInfo.setCommission(
		// ((float) (Math.round(commission * (productInfo.getIncomeRate() / 100)
		// * 100)) / 100));
		// if (commission <= 1) {
		// productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1")));
		// } else if (commission > 1 && commission <= 5) {
		// productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1-5")));
		// } else if (commission > 5 && commission <= 10) {
		// productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.5-10")));
		// } else if (commission > 10 && commission <= 50) {
		// productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.10-50")));
		// } else if (commission > 50 && commission <= 100) {
		// productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.50-100")));
		// } else if (commission > 100 && commission <= 500) {
		// productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.100-500")));
		// } else {
		// productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.500")));
		// }
		// }
		model.addAttribute("productInfoList", productInfoList);

		// model.addAttribute("saveMoney",GlobalVariable.resourceMap.get(DateUtil.dateFormate(new
		// Date(), DateUtil.CHINESE_PATTERN)));
		String date = DateUtil.dateFormate(new Date(), DateUtil.CHINESE_PATTERN);
		ShardedJedis jedis = jedisPool.getResource();
		model.addAttribute("saveMoney", jedis.get(date));
		jedis.close();

		return "searchv2/search";
	}

	@RequestMapping(value = "/searchmore", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchMore(Model model, HttpServletRequest request) {
		return "searchv2/more";
	}

	@ResponseBody
	@RequestMapping(value = "/api/more", method = { RequestMethod.GET, RequestMethod.POST })
	public JsonResult apiMore(Model model, HttpServletRequest request) {
		String mobile = CookieHelper.getCookie("mobile");
		String key = request.getParameter("product_url");
		long pageNo = RequestUtil.getLongParameter(request, "pageNo", 1);
		long size = RequestUtil.getLongParameter(request, "size", 20);
		JsonResult result = new JsonResult();
		String ua = request.getHeader("User-Agent");
		String ifWeixinBrower = "no";
		if ((ua.toLowerCase()).contains("micromessenger")) {
			ifWeixinBrower = "yes";
		}
		model.addAttribute("ifWeixinBrower", ifWeixinBrower);
		float rate = Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate"));
		model.addAttribute("rate", rate);

		if (StringUtil.isNotEmpty(key)) {
			// 插入搜索记录
			SearchRecord searchRecord = new SearchRecord();
			searchRecord.setMobile(mobile);
			searchRecord.setProductId("");
			searchRecord.setMall(1);
			searchRecord.setStatus(1);
			searchRecord.setTitle(key);
			searchRecord.setCreateTime(new Date());
			searchRecord.setUpdateTime(new Date());
			searchRecordService.insert(searchRecord);

			String retStr = MaterialSearch.materialSearch(key, pageNo, size);
			MaterialSearchVo materialSearchVo = GsonUtil.GsonToBean(retStr, MaterialSearchVo.class);
			List<MapDataBean> mapDataBeanList = materialSearchVo.getTbk_dg_material_optional_response().getResult_list()
					.getMap_data();
			long total_results = materialSearchVo.getTbk_dg_material_optional_response().getTotal_results();
			List<ProductInfo> productInfoList = new ArrayList<>();
			if (mapDataBeanList != null && mapDataBeanList.size() > 0) {
				String tkurl = "";
				for (MapDataBean mapDataBean : mapDataBeanList) {
					ProductInfo productInfo = new ProductInfo();
					productInfo.setProductName(mapDataBean.getTitle());
					productInfo.setProductImgUrl(mapDataBean.getPict_url());
					productInfo.setShopName(mapDataBean.getShop_title());
					productInfo.setPrice(Double.parseDouble(mapDataBean.getReserve_price()));
					productInfo.setZkPrice(Float.parseFloat(mapDataBean.getZk_final_price()));
					productInfo.setMonthSales(mapDataBean.getVolume().intValue());
					productInfo.setProductId(mapDataBean.getNum_iid().toString());
					productInfo.setPlatformType(mapDataBean.getUser_type() == 0 ? "淘宝" : "天猫");
					String quan = "";
					if (StringUtil.isNotEmpty(mapDataBean.getCoupon_info())) {
						productInfo.setCouponMiane(mapDataBean.getCoupon_info());
						productInfo.setCouponRest(mapDataBean.getCoupon_remain_count().intValue());
						Pattern p = Pattern.compile("减(\\d+)元");
						Matcher m = p.matcher(mapDataBean.getCoupon_info());
						if (m.find()) {
							quan = m.group(1);
							productInfo.setCouponQuan(quan);
						}
						p = Pattern.compile("(\\d+)元无条件券");
						m = p.matcher(mapDataBean.getCoupon_info());
						if (m.find()) {
							quan = m.group(1);
							productInfo.setCouponQuan(quan);
						}
						tkurl = mapDataBean.getCoupon_share_url();
						productInfo.setCouponPromoLink(tkurl);
					} else {
						tkurl = mapDataBean.getUrl();
						productInfo.setCouponPromoLink(tkurl);
					}
					String tklStr = TaoKouling.createTkl("https:" + tkurl, mapDataBean.getTitle(),
							mapDataBean.getPict_url());
					if (StringUtil.isNotEmpty(tklStr)) {
						TklResponse tklResponse = GsonUtil.GsonToBean(tklStr, TklResponse.class);
						productInfo.setTkl(tklResponse.getTbk_tpwd_create_response().getData().getModel());
					}
					float commission = 0f;
					float actualCommission = 0f;
					double actualPrice = 0d;
					double incomeRate = Double.parseDouble(mapDataBean.getCommission_rate()) / 100;
					if (StringUtil.isNotEmpty(quan)) {
						actualPrice = Double.parseDouble(mapDataBean.getZk_final_price()) - Double.parseDouble(quan);
					} else {
						actualPrice = Double.parseDouble(mapDataBean.getZk_final_price());
					}
					commission = ((float) (Math.round(actualPrice * (incomeRate) * 100) / 100) / 100);
					actualCommission = ((float) (Math.round(actualPrice * (incomeRate)
							* Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100) / 100) / 100);
					productInfo.setCommission(commission);
					productInfo.setActualCommission(actualCommission);

					if (actualCommission <= 1) {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1")));
					} else if (actualCommission > 1 && actualCommission <= 5) {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1-5")));
					} else if (actualCommission > 5 && actualCommission <= 10) {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.5-10")));
					} else if (actualCommission > 10 && actualCommission <= 50) {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.10-50")));
					} else if (actualCommission > 50 && actualCommission <= 100) {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.50-100")));
					} else if (actualCommission > 100 && actualCommission <= 500) {
						productInfo
								.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.100-500")));
					} else {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.500")));
					}

					productInfoList.add(productInfo);
				}
				result.setList(productInfoList);
				result.setCurPage((int) pageNo);
				result.setMaxPage(total_results / size + 1);
				result.setTolrow(total_results);
			}
		} else {
			// 通过接口取优惠群数据
			CouponSearchVo couponSearchVo = GsonUtil.GsonToBean(CouponGet.couponGet(null, pageNo, size),
					CouponSearchVo.class);
			List<CouponBean> couponBeanList = couponSearchVo.getTbk_dg_item_coupon_get_response().getResults()
					.getTbk_coupon();
			long total_results = couponSearchVo.getTbk_dg_item_coupon_get_response().getTotal_results();
			List<ProductInfo> productInfoList = new ArrayList<>();
			if (couponBeanList != null && couponBeanList.size() > 0) {
				String tkurl = "";
				for (CouponBean couponBean : couponBeanList) {
					ProductInfo productInfo = new ProductInfo();
					productInfo.setProductName(couponBean.getTitle());
					productInfo.setProductImgUrl(couponBean.getPict_url());
					productInfo.setShopName(couponBean.getShop_title());
					// productInfo.setPrice(Double.parseDouble(couponBean.getZk_final_price()));
					productInfo.setZkPrice(Float.parseFloat(couponBean.getZk_final_price()));
					productInfo.setMonthSales(couponBean.getVolume().intValue());
					productInfo.setProductId(couponBean.getNum_iid().toString());
					productInfo.setPlatformType(couponBean.getUser_type() == 0 ? "淘宝" : "天猫");
					String quan = "";
					if (StringUtil.isNotEmpty(couponBean.getCoupon_info())) {
						productInfo.setCouponMiane(couponBean.getCoupon_info());
						productInfo.setCouponRest(couponBean.getCoupon_remain_count().intValue());
						Pattern p = Pattern.compile("减(\\d+)元");
						Matcher m = p.matcher(couponBean.getCoupon_info());
						if (m.find()) {
							quan = m.group(1);
							productInfo.setCouponQuan(quan);
						}
						p = Pattern.compile("(\\d+)元无条件券");
						m = p.matcher(couponBean.getCoupon_info());
						if (m.find()) {
							quan = m.group(1);
							productInfo.setCouponQuan(quan);
						}
						tkurl = couponBean.getCoupon_click_url();
						productInfo.setCouponPromoLink(tkurl);
					}
					String tklStr = TaoKouling.createTkl(tkurl, couponBean.getTitle(), couponBean.getPict_url());
					if (StringUtil.isNotEmpty(tklStr)) {
						TklResponse tklResponse = GsonUtil.GsonToBean(tklStr, TklResponse.class);
						productInfo.setTkl(tklResponse.getTbk_tpwd_create_response().getData().getModel());
					}
					float commission = 0f;
					float actualCommission = 0f;
					double actualPrice = 0d;
					double incomeRate = Double.parseDouble(couponBean.getCommission_rate()) / 100;
					if (StringUtil.isNotEmpty(quan)) {
						actualPrice = Double.parseDouble(couponBean.getZk_final_price()) - Double.parseDouble(quan);
					} else {
						actualPrice = Double.parseDouble(couponBean.getZk_final_price());
					}
					commission = ((float) (Math.round(actualPrice * (incomeRate) * 100)) / 100);
					actualCommission = ((float) (Math.round(actualPrice * (incomeRate)
							* Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100)) / 100);
					productInfo.setCommission(commission);
					productInfo.setActualCommission(actualCommission);

					if (actualCommission <= 1) {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1")));
					} else if (actualCommission > 1 && actualCommission <= 5) {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1-5")));
					} else if (actualCommission > 5 && actualCommission <= 10) {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.5-10")));
					} else if (actualCommission > 10 && actualCommission <= 50) {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.10-50")));
					} else if (actualCommission > 50 && actualCommission <= 100) {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.50-100")));
					} else if (actualCommission > 100 && actualCommission <= 500) {
						productInfo
								.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.100-500")));
					} else {
						productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.500")));
					}

					productInfoList.add(productInfo);
				}
				result.setList(productInfoList);
				result.setCurPage((int) pageNo);
				result.setMaxPage(total_results / size + 1);
				result.setTolrow(total_results);
			}

			// 通过数据库取优惠券数据
			// SearchDataVo vo = SearchUtil.getVoForList();
			// if(StringUtil.isNotEmpty(key)){
			// vo.putSearchParam("productName", key, key);
			// }
			// productInfoService.selectProductInfoList(vo);
			// model.addAttribute("rate", rate);
			// @SuppressWarnings("unchecked")
			// List<ProductInfo> productInfoList1 = (List<ProductInfo>)
			// vo.getList();
			// for (ProductInfo productInfo : productInfoList1) {
			// double commission = productInfo.getCommission();
			// commission = (productInfo.getPrice() -
			// Double.parseDouble(productInfo.getCouponQuan()))*(productInfo.getIncomeRate()
			// / 100);
			// productInfo.setCommission(
			// ((float) (Math.round(commission * (productInfo.getIncomeRate() /
			// 100) * 100)) / 100));
			// productInfo.setActualCommission(((float) (Math
			// .round(commission * (productInfo.getIncomeRate() / 100) *
			// Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate"))
			// * 100))
			// / 100));
			// if (commission <= 1) {
			// productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1")));
			// } else if (commission > 1 && commission <= 5) {
			// productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1-5")));
			// } else if (commission > 5 && commission <= 10) {
			// productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.5-10")));
			// } else if (commission > 10 && commission <= 50) {
			// productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.10-50")));
			// } else if (commission > 50 && commission <= 100) {
			// productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.50-100")));
			// } else if (commission > 100 && commission <= 500) {
			// productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.100-500")));
			// } else {
			// productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.500")));
			// }
			// }
			// result.setList(productInfoList1);
			// result.setCurPage(vo.getStart());
			// result.setMaxPage(vo.getCount() / vo.getSize());
			// result.setTolrow(vo.getCount());
		}

		return result;
	}

	@RequestMapping(value = "/campaign", method = { RequestMethod.GET, RequestMethod.POST })
	public String campaign(Model model, HttpServletRequest request) {
		List<Banner> campaignAllList = bannerService.selectAll(2);
		// 即将开始列表
		List<Banner> campaignBeginInAMinuteList = new ArrayList<>();
		// 已开始列表
		List<Banner> campaignAlreadyStartedList = new ArrayList<>();
		// 已结束
		List<Banner> campaignAlreadyStopedList = new ArrayList<>();
		Date nowDate = new Date();
		if (campaignAllList != null && campaignAllList.size() > 0) {
			for (Banner banner : campaignAllList) {
				if (banner.getFromTime().getTime() > nowDate.getTime()) {
					banner.setDesc("即将开始");
					campaignBeginInAMinuteList.add(banner);
				} else if (banner.getFromTime().getTime() <= nowDate.getTime()
						&& banner.getToTime().getTime() > nowDate.getTime()) {
					banner.setDesc("已开始");
					campaignAlreadyStartedList.add(banner);
				} else {
					banner.setDesc("已结束");
					campaignAlreadyStopedList.add(banner);
				}
			}
		}

		model.addAttribute("campaignAllList", campaignAllList);
		model.addAttribute("campaignBeginInAMinuteList", campaignBeginInAMinuteList);
		model.addAttribute("campaignAlreadyStartedList", campaignAlreadyStartedList);
		model.addAttribute("campaignAlreadyStopedList", campaignAlreadyStopedList);
		return "searchv2/campaign";
	}
}
