package com.bt.om;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.shiro.crypto.hash.Md5Hash;

import com.bt.om.util.ConfigUtil;
import com.bt.om.util.DateUtil;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.HttpcomponentsUtil;
import com.bt.om.util.MailUtil;
import com.bt.om.util.StringUtil;

public class Test {

	public static void main(String[] args) {
		// List<NameValuePair> nvpList=new ArrayList<>();
		// nvpList.add(new
		// BasicNameValuePair("num_iids","555498592290,563823419762,563308542333"));
		// try {
		// String ret=HttpcomponentsUtil.postReq(nvpList,
		// "http://tae.xmluren.com/tae/shop");
		// List<String> list=GsonUtil.GsonToList(ret, String.class);
		// System.out.println(list.get(0));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// System.out.println(Float.valueOf("0.0"));

		// String ptCasinoMsg = "日单量：100 | 实付金额：5000.0 | 订单金额：57000.34 |
		// 优惠金额：9000";
		// String[] amounts = extractAmountMsg(ptCasinoMsg);
		// String ptliveOrderCount = amounts[0].toString();
		// String ptliveVilida = amounts[1].toString();
		// String ptliveSum = amounts[2].toString();
		// String ptlivePayout = amounts[3].toString();
		// System.out.println("日单量：" + ptliveOrderCount + " 实付金额：" +
		// ptliveVilida + " 订单金额：" + ptliveSum + " 优惠金额："
		// + ptlivePayout);

		// float ft=9.07f;

		// BigDecimal bd = new BigDecimal((double)(ft*0.6));
		// bd = bd.setScale(2,4);
		// ft = bd.floatValue();
		// System.out.println(ft);

		// float b = (float)(Math.round(ft*0.6*100))/100;
		// System.out.println((float) (Math
		// .round(35.81234f * 0.6 * 100))
		// / 100);
		// String md5Pwd = new Md5Hash("123456", "admin@adbest.com").toString();
		// System.out.println(md5Pwd);

		// System.out.println(StringUtil.getSubString("2018夏季男士短袖t恤韩版圆领纯色体恤学生打底衫潮上衣服帅气潮",
		// 20));

		//String productUrl = "https://item.m.jd.com/product/10883976348.html?&utm_source=iosapp&utm_medium=appshare&utm_campaign=t_335139774&utm_term=CopyURL";
		String productUrl="http://ccc-x.jd.com/dsp/nc.html?ext=aHR0cHM6Ly9pdGVtLmpkLmNvbS8yNjg4MDg3NDQ0Ni5odG1s&log=Tbnbg_l2JUx6Zf1HFIclnDLiblc4T6yeFN8svGovvqX-aq5Bdx7HMCAvuZ-pLmcFPqw6bXtYcywg5hkmjr15NAIjxS66W0wQli4kcbWlYDBmOAvHHVlRtdEXbe2wLg-0O_S_udhOuQiq6g1_JIYAw-zY5jXRDKrHvkgBClgFhxYlIYhG-MSxEnRgWR-Nm3QxIqC8c4U4VIp37Jiw_tMltuh1ebKmZAaU2ArtqWgCWzi4gLOjCw8kaG3PvpKgxurOh90dgek0w4OXZo3q0ksJr5m35QhiKtOvkPuUWJ_l4YzByQD9mVGFqr4_x1ksERMDE6F0-ThvWa5Dfnub7SKVob1yVWcYHPmtWYkAhtHNyQIFgm9XSloyQWpcETEmVB6y8_zEGF0tyUxSAjClTlbErg&v=404";
		Map<String, String> urlMap = StringUtil.urlSplit(productUrl);

		if(productUrl.contains("jd.com")){
			System.out.println("京东链接");
		}
		String uri=StringUtil.getUri(productUrl);
		System.out.println(uri);

		String action=uri.substring(uri.lastIndexOf("/")+1);
		if(action.contains(".")){
			System.out.println(uri.substring(uri.lastIndexOf("/")+1, uri.lastIndexOf(".")));
		}else{
			System.out.println(action);
		}

//		
		
		Map<String, String> mapUrl=StringUtil.urlSplit(productUrl);
		
		System.out.println(mapUrl.get("puri"));
		System.out.println(mapUrl.get("dd"));
		
//		List<String> tos=new ArrayList<>();
//		tos.add("13732203065@139.com");
//		MailUtil.sendEmail("提现通知","13732203065申请提现",tos);
	}


	public static String[] extractAmountMsg(String ptCasinoMsg) {
		String returnAmounts[] = new String[4];
		ptCasinoMsg = ptCasinoMsg.replace(" | ", " ");
		String[] amounts = ptCasinoMsg.split(" ");
		for (int i = 0; i < amounts.length; i++) {
			Pattern p = Pattern.compile("(\\d+\\.\\d+)");
			Matcher m = p.matcher(amounts[i]);
			if (m.find()) {
				returnAmounts[i] = m.group(1);
			} else {
				p = Pattern.compile("(\\d+)");
				m = p.matcher(amounts[i]);
				if (m.find()) {
					returnAmounts[i] = m.group(1);
				}
			}
		}
		return returnAmounts;
	}
}
