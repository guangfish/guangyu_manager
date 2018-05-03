package com.bt.om.vo.api;

import java.io.Serializable;

public class UserVo implements Serializable {

	private static final long serialVersionUID = -2670370534198934941L;
	private String userId;
	private String status;

	public UserVo(String userId, String status) {
		this.userId = userId;
		this.status = status;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
