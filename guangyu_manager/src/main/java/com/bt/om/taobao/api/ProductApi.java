package com.bt.om.taobao.api;

import com.bt.om.taobao.api.product.ProductInfoVo;
import com.bt.om.util.GsonUtil;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkItemInfoGetRequest;
import com.taobao.api.response.TbkItemInfoGetResponse;

public class ProductApi extends BaseApi{
	public static String getProductInfo(String productId){
		String retStr = "";
		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey, appSecret);
		TbkItemInfoGetRequest req = new TbkItemInfoGetRequest();
		req.setNumIids(productId);
		req.setPlatform(2L);
		TbkItemInfoGetResponse rsp = null;
		try{
			rsp = client.execute(req);
			retStr=rsp.getBody();
//			System.out.println(retStr);
		}catch(Exception e){
			e.printStackTrace();
		}		
		return retStr;
	}
	
	public static void main(String[] args) {
		String retStr=getProductInfo("557195724988");
		ProductInfoVo ProductInfoVo=GsonUtil.GsonToBean(retStr, ProductInfoVo.class);
		System.out.println(ProductInfoVo.getTbk_item_info_get_response().getResults().getN_tbk_item().get(0).getPict_url());
		
	}

}
