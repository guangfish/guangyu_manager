package com.bt.om.web.controller.api.v2.vo;

import java.util.HashMap;
import java.util.Map;

public class OrderDrawVo extends CommonVo {
	private Map<String, String> map = new HashMap<>();

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

}
