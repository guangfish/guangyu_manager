package com.bt.om.web.controller.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.system.GlobalVariable;
import com.bt.om.taobao.api.MapDataBean;
import com.bt.om.taobao.api.MaterialSearch;
import com.bt.om.taobao.api.MaterialSearchVo;
import com.bt.om.taobao.api.TaoKouling;
import com.bt.om.taobao.api.TklResponse;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.NumberUtil;
import com.bt.om.util.StringUtil;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.app.vo.ItemVo;
import com.bt.om.web.controller.app.vo.ProductInfoVo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 商品按关键词搜索Controller
 */
@Controller
@RequestMapping(value = "/app/api")
public class AppProductSearchController extends BasicController {

	@RequestMapping(value = "/productSearch", method = RequestMethod.POST)
	@ResponseBody
	public Model getSmsCode(Model model, HttpServletRequest request, HttpServletResponse response) {
		ProductInfoVo productInfoVo = null;
		String key = null;
		int pageNo = 1;
		int size = 30;
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("key") != null) {
				key = obj.get("key").getAsString();
			}
			if (obj.get("pageNo") != null) {
				pageNo = obj.get("pageNo").getAsInt();
			}
			if (obj.get("size") != null) {
				size = obj.get("size").getAsInt();
			}
		} catch (IOException e) {
			productInfoVo = new ProductInfoVo();
			productInfoVo.setStatus("1");
			productInfoVo.setDesc("系统繁忙，请稍后再试");
			productInfoVo.setData(new ItemVo());
			model.addAttribute("response", productInfoVo);
			return model;
		}
		
		if("全部".equals(key)){
			key="";
		}

		productInfoVo = productInfoApi(key, pageNo, size);
		if (productInfoVo == null) {
			productInfoVo = new ProductInfoVo();
			productInfoVo.setDesc("未查到商品信息");
			productInfoVo.setStatus("2");
			productInfoVo.setData(new ItemVo());
			model.addAttribute("response", productInfoVo);
			return model;
		}
		
		model.addAttribute("response", productInfoVo);

		return model;
	}

	// 通过淘宝API查询商品信息
	public ProductInfoVo productInfoApi(String key, int pageNo, int size) {
		ProductInfoVo productInfoVo = new ProductInfoVo();
		try {
			String retStr="";
			String cat="16,30,14,35,50010788,50020808,50002766,50010728,50006843,50022703";
			if("".equals(key)){
				retStr = MaterialSearch.materialSearch(key,cat, pageNo, size);
			}else{
				retStr = MaterialSearch.materialSearch(key, pageNo, size);
			}
			logger.info(retStr);
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
		} catch (Exception e) {
			logger.info("通过API接口查询不到商品，关键词为==>" + key);
			e.printStackTrace();
		}
		return productInfoVo;
	}
}
