package com.bt.om.test;

import com.bt.om.util.TaobaoSmsNewUtil;

public class SendSms {

	public static void main(String[] args) {
		//SELECT GROUP_CONCAT(a.mobile SEPARATOR ';') AS NAME FROM (SELECT DISTINCT(mobile) FROM user_order) a
		String mobiesStr = "13173678552;13216078106;13456707160;13588294304;13655819293;13656665843;13757145937;13858164287;15057521610;15067156452;15157310501;15356881667;15558158798;15857572158;15858165476;15868882771;17512562410;17542562410;17858802711;18768189267;18857189949;18868828397";
		String[] mobiles = mobiesStr.split(";");
		for (String mobile : mobiles) {
			System.out.println(mobile);
			TaobaoSmsNewUtil.sendSmsCommon("逛鱼返利", "SMS_135793100", mobile);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
