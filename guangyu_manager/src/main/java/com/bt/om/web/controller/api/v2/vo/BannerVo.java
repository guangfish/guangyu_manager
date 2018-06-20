package com.bt.om.web.controller.api.v2.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BannerVo extends CommonVo {
	private List<Map<String, String>> data=new ArrayList<>();

	public List<Map<String, String>> getData() {
		return data;
	}

	public void setData(List<Map<String, String>> data) {
		this.data = data;
	}


}
