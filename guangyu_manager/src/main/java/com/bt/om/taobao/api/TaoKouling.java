package com.bt.om.taobao.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkTpwdCreateRequest;
import com.taobao.api.request.WirelessShareTpwdQueryRequest;
import com.taobao.api.response.TbkTpwdCreateResponse;
import com.taobao.api.response.WirelessShareTpwdQueryResponse;


public class TaoKouling {
	private static String serverUrl="https://eco.taobao.com/router/rest";
	private static String appKey="24736090";
	private static String appSecret="8759042d314ec30a88a0d6e9668e7bfe";

	public static void main(String[] args) {
		System.out.println(parserTkl("€JBoW0BnOphj€"));
//		System.out.println(createTkl("https://s.click.taobao.com/t?e=m=2&s=iFpYkRAdNhZw4vFB6t2Z2ueEDrYVVa64qYbrUZilZ4UYX8TY+NEwd7GfWq0iAFqMbJxUEh8sgi9h0P5Awum43Texw+KOh7ITNWf5FlaIm5YwhCI/ziBObgnlfQyFtCJvm9ckqDpbvbi2h3NlB8wsea6YN/hjnMyqDJbuZDCrHt4=&scm=NULL&pvid=100_10.103.67.73_12231_9251529892760622368&app_pvid=0bfa8dac15298927606131596&union_lens=lensId:0bb3a96c_5fe0_16434b6ecc6_7c6d","儿童防蚊裤夏季男童夏装纯棉女童长裤子薄阔腿宝宝小童棉绸灯笼裤","https://img.alicdn.com/bao/uploaded/i3/1844656432/TB2gBD1rQCWBuNjy0FaXXXUlXXa_!!1844656432.jpg_220x220"));
	}
	
	public static String parserTklApp(String content) {
		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey,
				appSecret);
		WirelessShareTpwdQueryRequest req = new WirelessShareTpwdQueryRequest();
		req.setPasswordContent(content);
		WirelessShareTpwdQueryResponse rsp = null;
		String retStr="";
		String url="";
		String imgUrl="";
		try {
			rsp = client.execute(req);
			retStr=rsp.getBody();
			System.out.println(retStr);
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(retStr, JsonObject.class);		
			JsonObject obj1=obj.getAsJsonObject("wireless_share_tpwd_query_response");
			url=obj1.get("url").getAsString();
			imgUrl=obj1.get("thumb_pic_url").getAsString();
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return url+";;"+imgUrl;
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
			System.out.println(retStr);
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(retStr, JsonObject.class);		
			JsonObject obj1=obj.getAsJsonObject("wireless_share_tpwd_query_response");
			url=obj1.get("url").getAsString();
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	public static String createTkl(String url,String title,String pic) {
		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey, appSecret);
		TbkTpwdCreateRequest req = new TbkTpwdCreateRequest();
//		req.setUserId("123");
		req.setText(title);
		req.setUrl(url);
		req.setLogo(pic);
//		req.setExt("{}");
		TbkTpwdCreateResponse rsp;
		String tkl="";
		try {
			rsp = client.execute(req);
			tkl=rsp.getBody();
		} catch (ApiException e) {
			e.printStackTrace();
		}									
		return tkl;
	}

}
