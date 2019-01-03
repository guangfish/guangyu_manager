package com.bt.om.pdd.pid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.junit.Test;

import com.bt.om.entity.PddPids;
import com.bt.om.mapper.PddPidsMapper;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.HttpcomponentsUtil;

@ContextConfiguration(locations = { "classpath*:/applicationContext-test.xml" })
public class CreatePidTest extends AbstractJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private PddPidsMapper pddPidsMapper;

	@Test
	public void createPid() {
		while (true) {
			String remoteTaskUrl = "https://gw-api.pinduoduo.com/api/router";
			List<NameValuePair> nvpList = new ArrayList<>();
			String client_id = "b2c4f22ab0624617959b4782c56f0674";
			String client_secret = "bad4d916abfce80afa0cc6bfaa35cf721daab7cc";
			String timestamp = System.currentTimeMillis() / 1000L + "";
			String data_type = "JSON";
			String number = "100";
			String type = "pdd.ddk.goods.pid.generate";
			String dd = client_secret + "client_id" + client_id + "data_type" + data_type + "number" + number
					+ "timestamp" + timestamp + "type" + type + client_secret;
			String sign = getMD5String(dd);
			sign = sign.toUpperCase();
			remoteTaskUrl = remoteTaskUrl + "?type=" + type + "&sign=" + sign + "&client_id=" + client_id
					+ "&data_type=" + data_type + "&timestamp=" + timestamp + "&number=" + number;
			try {
				String ret = HttpcomponentsUtil.sendHttpsJson(nvpList, remoteTaskUrl);
//				System.out.println(ret);
//				Root root = GsonUtil.GsonToBean(ret, Root.class);
//				List<P_id_list> list = root.getP_id_generate_response().getP_id_list();
//				for (P_id_list p_id_list : list) {
//					String pid = p_id_list.getP_id();
//					PddPids pddPids=new PddPids();
//					pddPids.setPid(pid);
//					pddPids.setPidName(p_id_list.getPid_name());
//					pddPidsMapper.insert(pddPids);
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getMD5String(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			return new BigInteger(1, md.digest()).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
