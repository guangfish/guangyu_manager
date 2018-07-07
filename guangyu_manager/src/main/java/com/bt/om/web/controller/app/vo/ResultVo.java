package com.bt.om.web.controller.app.vo;

import java.util.HashMap;
import java.util.List;

import com.bt.om.web.controller.api.v2.vo.CommonVo;

public class ResultVo extends CommonVo {
	private List<HashMap<String, String>> data;

	public List<HashMap<String, String>> getData() {
		return data;
	}

	public void setData(List<HashMap<String, String>> data) {
		this.data = data;
	}

}
