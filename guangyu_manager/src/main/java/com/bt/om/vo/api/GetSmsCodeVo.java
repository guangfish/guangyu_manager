package com.bt.om.vo.api;

import java.io.Serializable;

public class GetSmsCodeVo implements Serializable {

	private static final long serialVersionUID = 5774873558751018950L;
	private String vcode;
	private String status;
	private String desc;
	
	public GetSmsCodeVo(){		
	}
	
	public GetSmsCodeVo(String vcode,String status) {
		this.vcode = vcode;
		this.status=status;
	}

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

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
