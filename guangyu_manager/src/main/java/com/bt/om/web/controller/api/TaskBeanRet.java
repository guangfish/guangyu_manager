package com.bt.om.web.controller.api;

import java.io.Serializable;

public class TaskBeanRet implements Serializable {

	private static final long serialVersionUID = -3637456440986915138L;
	private TaskBean ret;

	public TaskBean getRet() {
		return ret;
	}

	public void setRet(TaskBean ret) {
		this.ret = ret;
	}

}
