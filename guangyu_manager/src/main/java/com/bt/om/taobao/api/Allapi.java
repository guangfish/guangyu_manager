package com.bt.om.taobao.api;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bt.om.entity.ProductFromTkapi;
import com.bt.om.service.impl.ProductFromTkapiService;
import com.bt.om.task.CommissionTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkItemGetRequest;
import com.taobao.api.request.TbkItemInfoGetRequest;
import com.taobao.api.response.TbkItemGetResponse;
import com.taobao.api.response.TbkItemInfoGetResponse;

public class Allapi {
	private static String serverUrl="https://eco.taobao.com/router/rest";
	private static String appKey="24736090";
	private static String appSecret="8759042d314ec30a88a0d6e9668e7bfe";
	
	private static ProductFromTkapiService productFromTkapiService;
	static{
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		productFromTkapiService=(ProductFromTkapiService)ctx.getBean("productFromTkapiService");
	}

	public static void main(String[] args) {
//		System.out.println(getPage("女装",100));
		parserJson();
	}

	private static void getItemInfo() {
		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey,
				appSecret);
		TbkItemInfoGetRequest req = new TbkItemInfoGetRequest();
//		req.setFields("num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url");
		req.setPlatform(1L);
		req.setNumIids("559374142896");
		TbkItemInfoGetResponse rsp;
		try {
			rsp = client.execute(req);
			System.out.println(rsp.getBody());
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String getItemList(String key,long page,long size){
		String retStr="";
		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey,
				appSecret);
		TbkItemGetRequest req = new TbkItemGetRequest();
		req.setFields(
				"num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url,seller_id,volume,nick");
		req.setQ(key);
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
		req.setPageNo(page);
		req.setPageSize(size);
		TbkItemGetResponse rsp;
		try {
			rsp = client.execute(req);
			retStr=rsp.getBody();			
		} catch (ApiException e) {
//			e.printStackTrace();
			System.out.println("获取数据异常");
		}	
		return retStr;
	}
	
	private static long getPage(String key,int size){
		long page=0;
		String retStr=getItemList(key,1,2);
		while(true){
			if(!"".equals(retStr)){
				break;
			}else{
				retStr=getItemList(key,1,2);
				System.out.println("重新连接");
			}
		}
		Gson gson = new Gson();
		JsonObject obj = gson.fromJson(retStr, JsonObject.class);		
		JsonObject obj1=obj.getAsJsonObject("tbk_item_get_response");
		long totalResult=obj1.get("total_results").getAsLong();
//		System.out.println(totalResult);
		if(totalResult/size==0){
			page=totalResult/size;
		}else{
			page=totalResult/size+1;
		}
		return page;
	}
	
	private static void parserJson(){
		List<String> keyList=new ArrayList<>(Arrays.asList("童装","玩具","箱包","鞋靴","运动","户外","乐器"));
		for(String key:keyList){
			long page=getPage(key,100);
			if(page>=100){
				page=100;
			}
			System.out.println(key+"共"+page+"页");
			for(long i=0;i<page;i++){
				System.out.println("第"+(i+1)+"页");
				String retStr=getItemList(key,i+1,100);
				while(true){
					if(!"".equals(retStr)){
						break;
					}else{
						retStr=getItemList(key,i+1,100);
						System.out.println("重新连接");
					}
				}
//				System.out.println(retStr);
				Gson gson = new Gson();
				JsonObject obj = gson.fromJson(retStr, JsonObject.class);		
				JsonObject obj1=obj.getAsJsonObject("tbk_item_get_response");
				JsonObject obj2=obj1.getAsJsonObject("results");
				JsonArray obj3=obj2.getAsJsonArray("n_tbk_item");
//				System.out.println(obj1);
//				System.out.println(obj2);
//				System.out.println(obj3);
				//遍历JsonArray对象
			    Iterator it = obj3.iterator();
			    while(it.hasNext()){
			        JsonElement e = (JsonElement)it.next();
			        JsonObject obj4=e.getAsJsonObject();
			        ProductFromTkapi productFromTkapi=new ProductFromTkapi();
			        productFromTkapi.setItemUrl(obj4.get("item_url").getAsString());
			        productFromTkapi.setNick(obj4.get("nick").getAsString());
			        productFromTkapi.setNumIid(obj4.get("num_iid").getAsLong());
			        productFromTkapi.setPictUrl(obj4.get("pict_url").getAsString());
			        productFromTkapi.setProvcity(obj4.get("provcity").getAsString());
			        productFromTkapi.setReservePrice(obj4.get("reserve_price").getAsString());
			        productFromTkapi.setSellerId(obj4.get("seller_id").getAsString());
//			        productFromTkapi.setSmallImages(obj4.get("small_images").getAsString());
			        productFromTkapi.setTitle(obj4.get("title").getAsString());
			        productFromTkapi.setUserType(obj4.get("user_type").getAsInt());
			        productFromTkapi.setVolume(obj4.get("volume").getAsInt());
			        productFromTkapi.setZkFinalPrice(obj4.get("zk_final_price").getAsString());
			        productFromTkapi.setSkey(key);
			        try{
			        productFromTkapiService.insert(productFromTkapi);
			        }catch(Exception e1){
//			        	System.out.println("key 重复");
			        }
			    }
			}
		}
		
	}
}
