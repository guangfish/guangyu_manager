package com.bt.om.taobao.api.coupon;

import com.bt.om.util.GsonUtil;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkDgItemCouponGetRequest;
import com.taobao.api.response.TbkDgItemCouponGetResponse;

public class CouponGet {
	private static String serverUrl = "https://eco.taobao.com/router/rest";
	private static String appKey = "24736090";
	private static String appSecret = "8759042d314ec30a88a0d6e9668e7bfe";

	public static String couponGet(String key, long pageNo, long size) {
		String retStr = "";
		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey, appSecret);
		TbkDgItemCouponGetRequest req = new TbkDgItemCouponGetRequest();
		req.setAdzoneId(176864894L);
		req.setPlatform(2L);
		if (key != null) {
			req.setQ(key);
		}
		// req.setCat("16,18");
		req.setPageSize(size);
		// req.setQ("女装");
		req.setPageNo(pageNo);
		TbkDgItemCouponGetResponse rsp;
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
		// System.out.println(couponGet(null,1,30));
		CouponSearchVo couponSearchVo = GsonUtil.GsonToBean(couponGet("范姿牛仔连衣裙女中长款2018新款时尚短袖POLO领开叉修身裙子女夏", 1, 30), CouponSearchVo.class);
		System.out.println(couponSearchVo.getTbk_dg_item_coupon_get_response().getResults().getTbk_coupon().size());
	}

}
