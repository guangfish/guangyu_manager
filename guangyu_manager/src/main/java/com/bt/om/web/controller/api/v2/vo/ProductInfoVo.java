package com.bt.om.web.controller.api.v2.vo;

import java.util.Map;

public class ProductInfoVo extends CommonVo {
	private String mall = "";
	private Map<String, String> map;

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public String getMall() {
		return mall;
	}

	public void setMall(String mall) {
		this.mall = mall;
	}

}
