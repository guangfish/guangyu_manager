package com.bt.om.test;

import com.bt.om.util.HttpcomponentsUtil;

public class TestGetImg {

	public static void main(String[] args) {
		try {
			String ret = HttpcomponentsUtil.getReq("http://img.alicdn.com/bao/uploaded/i3/3011374894/TB1qdD1dqSWBuNjSsrbXXa0mVXa_!!0-item_pic.jpg");
		    System.out.println(ret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
