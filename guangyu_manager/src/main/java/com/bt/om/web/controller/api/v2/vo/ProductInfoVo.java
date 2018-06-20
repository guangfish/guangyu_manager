package com.bt.om.web.controller.api.v2.vo;

import java.util.Map;

public class ProductInfoVo extends CommonVo {
	private String mall = "";
	private Map<String, String> data;

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public String getMall() {
		return mall;
	}

	public void setMall(String mall) {
		this.mall = mall;
	}

}
