package com.bt.om.taobao.api;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkItemGetRequest;
import com.taobao.api.response.TbkItemGetResponse;

public class ItemGet {

	public static void main(String[] args) {
		TaobaoClient client = new DefaultTaobaoClient("https://eco.taobao.com/router/rest", "24736090", "ffbf69603078e77d886d023761f1541b");
		TbkItemGetRequest req = new TbkItemGetRequest();
		req.setFields(
				"num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url,seller_id,volume,nick");
		req.setQ("男装");
//		req.setCat("16,18");
//		req.setItemloc("杭州");
//		req.setSort("tk_rate_des");
//		req.setIsTmall(false);
//		req.setIsOverseas(false);
//		req.setStartPrice(10L);
//		req.setEndPrice(10L);
//		req.setStartTkRate(123L);
//		req.setEndTkRate(123L);
//		req.setPlatform(1L);
		req.setPageNo(1L);
		req.setPageSize(100L);
		TbkItemGetResponse rsp;
		try {
			rsp = client.execute(req);
			System.out.println(rsp.getBody());
		} catch (ApiException e) {
			e.printStackTrace();
		}		
	}

}
