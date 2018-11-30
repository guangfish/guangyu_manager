package com.bt.om.report.vo.taobao;

public class RootMore {
	private int code;

	private DataMore data;

	private String msg;
	
	private Err err;

	public void setCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

	public void setData(DataMore data) {
		this.data = data;
	}

	public DataMore getData() {
		return this.data;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}

	public Err getErr() {
		return err;
	}

	public void setErr(Err err) {
		this.err = err;
	}

}