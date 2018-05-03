package com.bt.om.web.controller.api;

import java.io.Serializable;
import java.util.Map;

public class TaskBean implements Serializable {
	private static final long serialVersionUID = -6752081149963716760L;
	
	private boolean succ;
	private String msg;
	private Map<String, String> map;

	public boolean getSucc() {
		return succ;
	}

	public void setSucc(boolean succ) {
		this.succ = succ;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

}
