package com.bt.om.report.vo.taobao;

public class RootErr {
	private int code;

	private String data;

	private String msg;
	
	private Err err;

	public void setCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
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