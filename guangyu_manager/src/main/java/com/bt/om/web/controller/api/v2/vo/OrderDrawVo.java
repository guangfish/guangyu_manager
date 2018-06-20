package com.bt.om.web.controller.api.v2.vo;

import java.util.HashMap;
import java.util.Map;

public class OrderDrawVo extends CommonVo {
	private Map<String, String> data = new HashMap<>();

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

}
