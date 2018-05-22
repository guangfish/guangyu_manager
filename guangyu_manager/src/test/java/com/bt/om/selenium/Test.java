package com.bt.om.selenium;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import com.bt.om.util.HttpcomponentsUtil;

public class Test {

	public static void main(String[] args) {
		List<NameValuePair> nvpList = new ArrayList<>();
		String ret = "";
		while(true){			
			try {
				ret = HttpcomponentsUtil.postReq(nvpList, "http://localhost:8082/api/gettask");
				System.out.println(ret);
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
