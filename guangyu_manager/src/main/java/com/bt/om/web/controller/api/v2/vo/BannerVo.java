package com.bt.om.web.controller.api.v2.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BannerVo extends CommonVo {
	private List<Map<String, String>> map=new ArrayList<>();

	public List<Map<String, String>> getMap() {
		return map;
	}

	public void setMap(List<Map<String, String>> map) {
		this.map = map;
	}

}
