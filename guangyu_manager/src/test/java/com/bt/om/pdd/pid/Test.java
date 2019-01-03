package com.bt.om.pdd.pid;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.bt.om.util.GsonUtil;
import com.bt.om.util.HttpcomponentsUtil;
import com.bt.om.web.controller.api.TkInfoTaskRet;

public class Test {

	public static void main(String[] args) {
		String remoteTaskUrl = "https://gw-api.pinduoduo.com/api/router";
		List<NameValuePair> nvpList = new ArrayList<>();
		String client_id = "bd2cf4372185489f85ccd7872de34028";
		String client_secret = "9cc7228b69776d52923051f92841b3daa7b5a012";
		String timestamp = System.currentTimeMillis() / 1000L + "";
		String data_type = "JSON";
		String number = "2";
		String type = "pdd.ddk.goods.pid.generate";
		String dd = client_secret + "client_id" + client_id + "data_type" + data_type + "number" + number + "timestamp"
				+ timestamp + "type" + type + client_secret;
		String sign = getMD5String(dd);
		sign = sign.toUpperCase();
		remoteTaskUrl = remoteTaskUrl + "?type=" + type + "&sign=" + sign + "&client_id=" + client_id + "&data_type="
				+ data_type + "&timestamp=" + timestamp + "&number=" + number;
		try {
			String ret = HttpcomponentsUtil.sendHttpsJson(nvpList, remoteTaskUrl);
			System.out.println(ret);
			Root root = GsonUtil.GsonToBean(ret, Root.class);
			System.out.println(root.getP_id_generate_response().getP_id_list().get(0).getP_id());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getMD5String(String str) {
		try {
			// 生成一个MD5加密计算摘要
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(str.getBytes());
			// digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
			// 一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方）
			return new BigInteger(1, md.digest()).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
