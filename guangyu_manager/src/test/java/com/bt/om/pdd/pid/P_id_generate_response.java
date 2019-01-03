package com.bt.om.pdd.pid;

import java.util.List;

public class P_id_generate_response {
	private List<P_id_list> p_id_list;

	private String request_id;

	public void setP_id_list(List<P_id_list> p_id_list) {
		this.p_id_list = p_id_list;
	}

	public List<P_id_list> getP_id_list() {
		return this.p_id_list;
	}

	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}

	public String getRequest_id() {
		return this.request_id;
	}
}
