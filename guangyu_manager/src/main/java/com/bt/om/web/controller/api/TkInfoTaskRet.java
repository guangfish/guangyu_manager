package com.bt.om.web.controller.api;

import java.io.Serializable;

import com.bt.om.entity.TkInfoTask;

public class TkInfoTaskRet implements Serializable {

	private static final long serialVersionUID = -3637456440986915138L;
	private TkInfoTask ret;

	public TkInfoTask getRet() {
		return ret;
	}

	public void setRet(TkInfoTask ret) {
		this.ret = ret;
	}

}
