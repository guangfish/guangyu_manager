package com.bt.om.web.controller.xcx.util;

import com.bt.om.taobao.api.BaseApi;
import com.bt.om.taobao.api.MaterialSearchVo;
import com.bt.om.taobao.api.SearchVo;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.StringUtil;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkDgMaterialOptionalRequest;
import com.taobao.api.response.TbkDgMaterialOptionalResponse;

public class XcxMaterialSearch extends BaseApi {
	
	public static String materialSearch(SearchVo searchVo) {
		String retStr = "";
		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey, appSecret);
		TbkDgMaterialOptionalRequest req = new TbkDgMaterialOptionalRequest();
		req.setPageSize(searchVo.getSize());
		req.setPageNo(searchVo.getPage());
		req.setPlatform(2L);
		req.setSort(searchVo.getSort());
		if(StringUtil.isNotEmpty(searchVo.getCat())){
			req.setCat(searchVo.getCat());
		}else{	
		    req.setQ(searchVo.getKey());
		}
		if(searchVo.getHasCoupon()==1){
			req.setHasCoupon(true);
		}
		req.setAdzoneId(Long.parseLong(searchVo.getPid()));
		TbkDgMaterialOptionalResponse rsp;
		try {
			rsp = client.execute(req);
			retStr = rsp.getBody();
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return retStr;
	}

	public static void main(String[] args) {
//		String retStr = materialSearch("", "16,30,14,35,50010788,50020808,50002766,50010728,50006843,50022703",1, 1l,
//				20l,"zk_total_sales");
//		MaterialSearchVo materialSearchVo = GsonUtil.GsonToBean(retStr, MaterialSearchVo.class);
//		System.out.println(materialSearchVo.getTbk_dg_material_optional_response().getResult_list().getMap_data().get(0)
//				.getSmall_images().getString()[1]);

	}

}
