package com.bt.om.web.controller.api.v2.vo;

import java.io.Serializable;

public class CommonVo implements Serializable {
	private static final long serialVersionUID = 2777615270923243898L;
	private String status = "0";
	private String desc = "";

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
