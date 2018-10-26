package com.bt.om.taobao.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bt.om.system.GlobalVariable;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.NumberUtil;
import com.bt.om.util.StringUtil;
import com.bt.om.web.controller.app.vo.ItemVo;
import com.bt.om.web.controller.app.vo.ProductInfoVo;

public class ProductSearchUtil {

	// 通过淘宝API查询商品信息
	public static ProductInfoVo productInfoApi(String key, int pageNo, int size) {
		ProductInfoVo productInfoVo = null;
		try {
			productInfoVo = new ProductInfoVo();
			long startTime = System.currentTimeMillis();
			String retStr = "";
			String cat = "16,30,14,35,50010788,50020808,50002766,50010728,50006843,50022703";
			if ("".equals(key)) {
				retStr = MaterialSearch.materialSearch(key, cat, pageNo, size);
			} else {
				retStr = MaterialSearch.materialSearch(key, pageNo, size);
			}
			System.out.println("调用接口执行时间" + (System.currentTimeMillis() - startTime));

			startTime = System.currentTimeMillis();
			MaterialSearchVo materialSearchVo = GsonUtil.GsonToBean(retStr, MaterialSearchVo.class);
			List<MapDataBean> mapDataBeanList = materialSearchVo.getTbk_dg_material_optional_response().getResult_list()
					.getMap_data();
			System.out.println("解析返回数据时间" + (System.currentTimeMillis() - startTime));
			long total_results = materialSearchVo.getTbk_dg_material_optional_response().getTotal_results();
			List<Map<String, String>> list = new ArrayList<>();

			startTime = System.currentTimeMillis();
			if (mapDataBeanList != null && mapDataBeanList.size() > 0) {
				String tkurl = "";
				for (MapDataBean mapDataBean : mapDataBeanList) {
					Map<String, String> map = new HashMap<>();
					map.put("imgUrl", mapDataBean.getSmall_images().getString()[0]);
					map.put("shopName", mapDataBean.getShop_title());
					map.put("productName", mapDataBean.getTitle());
					map.put("price", Float.parseFloat(mapDataBean.getZk_final_price()) + "");
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

					if (!tkurl.startsWith("http")) {
						tkurl = "https:" + tkurl;
					}
					map.put("tkUrl", tkurl);

					String tklStr = TaoKouling.createTkl(tkurl,
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

				productInfoVo.setData(itemVo);
			}
			System.out.println("解析数据封装对象" + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productInfoVo;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
