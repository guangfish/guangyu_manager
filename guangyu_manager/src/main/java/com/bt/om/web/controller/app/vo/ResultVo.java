package com.bt.om.web.controller.app.vo;


import com.bt.om.web.controller.api.v2.vo.CommonVo;

public class ResultVo extends CommonVo {

	private ItemVo data;

	public ItemVo getData() {
		return data;
	}

	public void setData(ItemVo data) {
		this.data = data;
	}


}
