package com.bt.om.taobao.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.WirelessShareTpwdQueryRequest;
import com.taobao.api.response.WirelessShareTpwdQueryResponse;

public class TaoKouling {
	private static String serverUrl="https://eco.taobao.com/router/rest";
	private static String appKey="24736090";
	private static String appSecret="8759042d314ec30a88a0d6e9668e7bfe";

	public static void main(String[] args) {
		System.out.println(parserTkl("￥mKlo0GdXIsN￥"));
	}
	
	public static String parserTkl(String content) {
		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey,
				appSecret);
		WirelessShareTpwdQueryRequest req = new WirelessShareTpwdQueryRequest();
		req.setPasswordContent(content);
		WirelessShareTpwdQueryResponse rsp = null;
		String retStr="";
		String url="";
		try {
			rsp = client.execute(req);
			retStr=rsp.getBody();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(retStr, JsonObject.class);		
			JsonObject obj1=obj.getAsJsonObject("wireless_share_tpwd_query_response");
			url=obj1.get("url").getAsString();
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return url;
	}

}
