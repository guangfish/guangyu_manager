package com.bt.om.web.controller.xcx.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.bt.om.system.GlobalVariable;
import com.bt.om.taobao.api.MapDataBean;
import com.bt.om.taobao.api.MaterialSearchVo;
import com.bt.om.taobao.api.SearchVo;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.NumberUtil;
import com.bt.om.util.StringUtil;
import com.bt.om.web.controller.xcx.XcxSearchController;

public class XcxProductSearchUtil {
	private static final Logger logger = Logger.getLogger(XcxSearchController.class);

	// 通过淘宝API查询商品信息
	public static ProductInfoVo productInfoApi(String key, int isSearch,String userId,String pid, int pageNo, int size, String sort) {
		ProductInfoVo productInfoVo = null;
		try {
			String retStr = "";
			String cat = GlobalVariable.resourceMap.get("taobao_search_cat");
			//用户在没有登陆状态下，默认广告位ID
			String defalutPid=ConfigUtil.getString("alimama.abigpush.default.pid", "176864894");
			SearchVo searchVo=new SearchVo();						
			if(StringUtil.isEmpty(pid)){
				pid=defalutPid;
			}	
			searchVo.setPid(pid);
			searchVo.setPage(pageNo);
			searchVo.setSize(size);
			//非搜索请求时，查询有优惠券的商品
			if(isSearch==1){
				searchVo.setHasCoupon(1);
			}
			
			if ("".equals(key)) {
				searchVo.setCat(cat);
			}else{
				searchVo.setKey(key);
			}
			retStr = XcxMaterialSearch.materialSearch(searchVo);

			MaterialSearchVo materialSearchVo = GsonUtil.GsonToBean(retStr, MaterialSearchVo.class);
			List<MapDataBean> mapDataBeanList = materialSearchVo.getTbk_dg_material_optional_response().getResult_list()
					.getMap_data();

			long total_results = materialSearchVo.getTbk_dg_material_optional_response().getTotal_results();
			List<Map<String, String>> list = new ArrayList<>();

			if (mapDataBeanList != null && mapDataBeanList.size() > 0) {
				String tkurl = "";
				for (MapDataBean mapDataBean : mapDataBeanList) {
					Map<String, String> map = new HashMap<>();
					map.put("imgUrl", mapDataBean.getPict_url() + "_290x290.jpg");
					map.put("imgBigUrl", mapDataBean.getPict_url() + "_800x800.jpg");
					if (mapDataBean.getSmall_images() != null && mapDataBean.getSmall_images().getString().length > 0) {
						map.put("smallImgUrls", Arrays.toString(mapDataBean.getSmall_images().getString()));
					} else {
						map.put("smallImgUrls", "["+mapDataBean.getPict_url() + "_800x800.jpg"+"]");
					}

					map.put("shopType", mapDataBean.getUser_type() + "");// 卖家类型，0表示集市，1表示商城
					map.put("shopName", mapDataBean.getShop_title());
					map.put("productTitle", mapDataBean.getTitle());
					map.put("productShortTitle", mapDataBean.getShort_title());
					map.put("categoryName", mapDataBean.getCategory_name());
					map.put("price", Float.parseFloat(mapDataBean.getZk_final_price()) + "");
					map.put("reservePrice", Float.parseFloat(mapDataBean.getReserve_price()) + "");
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
					if(StringUtil.isNotEmpty(userId)){
						map.put("commission", actualCommission + "");
					}else{
						map.put("commission", "");
					}					

					if (!tkurl.startsWith("http")) {
						tkurl = "https:" + tkurl;
					}
					map.put("tkUrl", tkurl);
					map.put("productId", mapDataBean.getNum_iid() + "");

					float pre = Float.parseFloat(NumberUtil.formatDouble(
							incomeRate * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")), "0.00"));
					map.put("per", pre + "");

					if ((int) actualCommission > 0) {
						list.add(map);
					}
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

				productInfoVo = new ProductInfoVo();
				productInfoVo.setData(itemVo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productInfoVo;
	}

}
