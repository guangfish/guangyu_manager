package com.bt.om.web.controller.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bt.om.cache.JedisPool;
import com.bt.om.system.GlobalVariable;
import com.bt.om.taobao.api.MapDataBean;
import com.bt.om.taobao.api.MaterialSearch;
import com.bt.om.taobao.api.MaterialSearchVo;
import com.bt.om.taobao.api.TaoKouling;
import com.bt.om.taobao.api.TklResponse;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.NumberUtil;
import com.bt.om.util.StringUtil;
import com.bt.om.web.controller.app.vo.ItemVo;
import com.bt.om.web.controller.app.vo.ProductInfoVo;

public class ProductSearchUtil {

	// 通过淘宝API查询商品信息
	public static ProductInfoVo productInfoApi(JedisPool jedisPool,String pid,String key, int pageNo, int size) {
		ProductInfoVo productInfoVo = null;
		try {
			long startTime = System.currentTimeMillis();
			String retStr = "";
			String cat = "16,30,14,35,50010788,50020808,50002766,50010728,50006843,50022703";
			if ("".equals(key)) {
				retStr = MaterialSearch.materialSearch(key, cat,pid, pageNo, size);
			} else {
				retStr = MaterialSearch.materialSearch(key,pid, pageNo, size);
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
//					if (mapDataBean.getSmall_images() != null) {
//						if (mapDataBean.getSmall_images().getString().length <= 0) {
//							map.put("imgUrl", mapDataBean.getPict_url());
//						} else {
//							map.put("imgUrl", mapDataBean.getSmall_images().getString()[0]);
//						}
//					} else {
//						map.put("imgUrl", mapDataBean.getPict_url());
//					}
					
					map.put("imgUrl", mapDataBean.getPict_url()+"_290x290.jpg");
					if (mapDataBean.getSmall_images() != null && mapDataBean.getSmall_images().getString().length > 0) {
						map.put("smallImgUrls", Arrays.toString(mapDataBean.getSmall_images().getString()));
					} else {
						map.put("smallImgUrls", "");
					}

					map.put("shopType", mapDataBean.getUser_type()+"");//卖家类型，0表示集市，1表示商城
					map.put("shopName", mapDataBean.getShop_title());
					map.put("productName", mapDataBean.getTitle());
					map.put("productShortName", mapDataBean.getShort_title());
					map.put("price", Float.parseFloat(mapDataBean.getZk_final_price()) + "");  //折后价
					map.put("reservePrice", Float.parseFloat(mapDataBean.getReserve_price()) + "");  //原价
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

					// String tklStr = TaoKouling.createTkl(tkurl,
					// "【预估返:" + actualCommission + "】" +
					// mapDataBean.getTitle(), mapDataBean.getPict_url());
					// if (StringUtil.isNotEmpty(tklStr)) {
					// TklResponse tklResponse = GsonUtil.GsonToBean(tklStr,
					// TklResponse.class);
					// map.put("tkl",
					// tklResponse.getTbk_tpwd_create_response().getData().getModel());
					// }

					map.put("title", mapDataBean.getTitle());
					map.put("pictUrl", mapDataBean.getPict_url());
					map.put("productId", mapDataBean.getNum_iid() + "");

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

				BlockingQueue<Map<String, String>> queue = new LinkedBlockingQueue<>();
				for (Map<String, String> map : list) {
					queue.put(map);
				}

				// 启动固定线程数据模式
				for (int i = 0; i < 10; i++) {
					System.out.println("启动线程" + i);
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							Map<String, String> map = null;
							Object redisTklObj = null;
							String tklStr = "";
							while (true) {
								try {
									map = queue.remove();
									redisTklObj = jedisPool.getFromCache("tkl", map.get("productId"));
									if (redisTklObj != null) {
										System.out.println(map.get("productId")+"缓存命中了。。。");
										tklStr = (String) redisTklObj;
										map.put("tkl", tklStr);
									} else {
										tklStr = TaoKouling.createTkl(map.get("tkUrl"), map.get("title"),
												map.get("pictUrl"));
										if (StringUtil.isNotEmpty(tklStr)) {
											TklResponse tklResponse = GsonUtil.GsonToBean(tklStr, TklResponse.class);
											map.put("tkl",
													tklResponse.getTbk_tpwd_create_response().getData().getModel());
											jedisPool.putInCache("tkl", map.get("productId"),
													tklResponse.getTbk_tpwd_create_response().getData().getModel(),
													Integer.parseInt(GlobalVariable.resourceMap.get("tkl_valid_time")) * 24 * 60 * 60);
										}
									}
								} catch (Exception e) {
//									e.printStackTrace();
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
