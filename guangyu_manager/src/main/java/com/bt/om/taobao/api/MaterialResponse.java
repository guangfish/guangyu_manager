package com.bt.om.taobao.api;

import java.io.Serializable;

public class MaterialResponse implements Serializable {
	private static final long serialVersionUID = 7128443712392311992L;
	private MaterialResultList result_list;
	private Long total_results;
	private String request_id;

	public MaterialResultList getResult_list() {
		return result_list;
	}

	public void setResult_list(MaterialResultList result_list) {
		this.result_list = result_list;
	}

	public Long getTotal_results() {
		return total_results;
	}

	public void setTotal_results(Long total_results) {
		this.total_results = total_results;
	}

	public String getRequest_id() {
		return request_id;
	}

	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}

}
