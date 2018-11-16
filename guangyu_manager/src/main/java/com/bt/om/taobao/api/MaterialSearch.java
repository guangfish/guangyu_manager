package com.bt.om.taobao.api;

import com.bt.om.util.GsonUtil;
import com.bt.om.util.StringUtil;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkDgMaterialOptionalRequest;
import com.taobao.api.response.TbkDgMaterialOptionalResponse;

public class MaterialSearch extends BaseApi{

//	public static String materialSearch(SearchVo searchVo) {
//		String retStr = "";
//		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey, appSecret);
//		TbkDgMaterialOptionalRequest req = new TbkDgMaterialOptionalRequest();
//		// req.setStartDsr(10L);
//		req.setPageSize(searchVo.getSize());
//		req.setPageNo(searchVo.getPage());
//		req.setPlatform(2L);
//		// req.setEndTkRate(1234L);
//		// req.setStartTkRate(1234L);
//		// req.setEndPrice(10L);
//		// req.setStartPrice(10L);
//		// req.setIsOverseas(false);
//		// req.setIsTmall(false);
//		req.setSort(searchVo.getSort());
//		// req.setItemloc("杭州");
//		// req.setCat("16,18");
//		req.setQ(searchVo.getKey());
//		if(searchVo.getHasCoupon()==1){
//			req.setHasCoupon(true);
//		}
//		// req.setIp("13.2.33.4");
//		req.setAdzoneId(Long.parseLong(searchVo.getPid()));
////		req.setAdzoneId(176864894L);
//		// req.setNeedFreeShipment(true);
//		// req.setNeedPrepay(true);
//		// req.setIncludePayRate30(true);
//		// req.setIncludeGoodRate(true);
//		// req.setIncludeRfdRate(true);
//		// req.setNpxLevel(2L);
//		TbkDgMaterialOptionalResponse rsp;
//		try {
//			rsp = client.execute(req);
//			retStr = rsp.getBody();
////			System.out.println(retStr);
//		} catch (ApiException e) {
//			e.printStackTrace();
//		}
//		return retStr;
//	}
	
	public static String materialSearch(SearchVo searchVo) {
		String retStr = "";
		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey, appSecret);
		TbkDgMaterialOptionalRequest req = new TbkDgMaterialOptionalRequest();
		// req.setStartDsr(10L);
		req.setPageSize(searchVo.getSize());
		req.setPageNo(searchVo.getPage());
		req.setPlatform(2L);
		// req.setEndTkRate(1234L);
		// req.setStartTkRate(1234L);
		// req.setEndPrice(10L);
		// req.setStartPrice(10L);
		// req.setIsOverseas(false);
		// req.setIsTmall(false);
		req.setSort(searchVo.getSort());
		// req.setItemloc("杭州");
		if(StringUtil.isNotEmpty(searchVo.getCat())){
			req.setCat(searchVo.getCat());
		}else{	
		    req.setQ(searchVo.getKey());
		}
		if(searchVo.getHasCoupon()==1){
			req.setHasCoupon(true);
		}
		// req.setIp("13.2.33.4");
		req.setAdzoneId(Long.parseLong(searchVo.getPid()));
//		req.setAdzoneId(176864894L);
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
		SearchVo searchVo=new SearchVo();
		searchVo.setKey("");
		searchVo.setCat("16,30,14,35,50010788,50020808,50002766,50010728,50006843,50022703");
		searchVo.setPid("176864894");
		searchVo.setPage(1l);
		searchVo.setSize(20l);
		String retStr = materialSearch(searchVo);

		MaterialSearchVo materialSearchVo = GsonUtil.GsonToBean(retStr, MaterialSearchVo.class);
		System.out.println(materialSearchVo.getTbk_dg_material_optional_response().getResult_list().getMap_data().get(0).getPict_url());

	}

}
