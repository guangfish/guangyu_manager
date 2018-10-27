package com.bt.om.taobao.api;

import java.util.Arrays;

import com.bt.om.util.GsonUtil;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkDgMaterialOptionalRequest;
import com.taobao.api.response.TbkDgMaterialOptionalResponse;

public class MaterialSearch {
	private static String serverUrl = "https://eco.taobao.com/router/rest";
	private static String appKey = "24736090";
	private static String appSecret = "8759042d314ec30a88a0d6e9668e7bfe";

	public static String materialSearch(String key,long pageNo,long size) {
		String retStr = "";
		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey, appSecret);
		TbkDgMaterialOptionalRequest req = new TbkDgMaterialOptionalRequest();
		// req.setStartDsr(10L);
		req.setPageSize(size);
		req.setPageNo(pageNo);
		req.setPlatform(2L);
		// req.setEndTkRate(1234L);
		// req.setStartTkRate(1234L);
		// req.setEndPrice(10L);
		// req.setStartPrice(10L);
		// req.setIsOverseas(false);
		// req.setIsTmall(false);
		req.setSort("total_sales");
		// req.setItemloc("杭州");
		// req.setCat("16,18");
		req.setQ(key);
		//req.setHasCoupon(true);
		// req.setIp("13.2.33.4");
		req.setAdzoneId(176864894L);
		// req.setNeedFreeShipment(true);
		// req.setNeedPrepay(true);
		// req.setIncludePayRate30(true);
		// req.setIncludeGoodRate(true);
		// req.setIncludeRfdRate(true);
		// req.setNpxLevel(2L);
		TbkDgMaterialOptionalResponse rsp;
		try {
			rsp = client.execute(req);
			retStr = rsp.getBody();
//			System.out.println(retStr);
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return retStr;
	}
	
	public static String materialSearch(String key,String cat,long pageNo,long size) {
		String retStr = "";
		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey, appSecret);
		TbkDgMaterialOptionalRequest req = new TbkDgMaterialOptionalRequest();
		// req.setStartDsr(10L);
		req.setPageSize(size);
		req.setPageNo(pageNo);
		req.setPlatform(2L);
		// req.setEndTkRate(1234L);
		// req.setStartTkRate(1234L);
		// req.setEndPrice(10L);
		// req.setStartPrice(10L);
		// req.setIsOverseas(false);
		// req.setIsTmall(false);
		req.setSort("total_sales");
		// req.setItemloc("杭州");
		req.setCat(cat);
//		req.setQ(key);
		//req.setHasCoupon(true);
		// req.setIp("13.2.33.4");
		req.setAdzoneId(176864894L);
		// req.setNeedFreeShipment(true);
		// req.setNeedPrepay(true);
		// req.setIncludePayRate30(true);
		// req.setIncludeGoodRate(true);
		// req.setIncludeRfdRate(true);
		// req.setNpxLevel(2L);
		TbkDgMaterialOptionalResponse rsp;
		try {
			rsp = client.execute(req);
			retStr = rsp.getBody();
//			System.out.println(retStr);
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return retStr;
	}

	public static void main(String[] args) {
		String retStr = materialSearch("","16,30,14,35,50010788,50020808,50002766,50010728,50006843,50022703",1l,20l);

		MaterialSearchVo materialSearchVo = GsonUtil.GsonToBean(retStr, MaterialSearchVo.class);
		System.out.println(materialSearchVo.getTbk_dg_material_optional_response().getResult_list().getMap_data().get(0).getPict_url());

	}

}
