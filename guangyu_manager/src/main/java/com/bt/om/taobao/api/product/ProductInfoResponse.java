package com.bt.om.taobao.api.product;

import java.io.Serializable;

public class ProductInfoResponse implements Serializable {
	private static final long serialVersionUID = 8937559654210317699L;
	private ProductInfoResult results;
	private String request_id;

	public ProductInfoResult getResults() {
		return results;
	}

	public void setResults(ProductInfoResult results) {
		this.results = results;
	}

	public String getRequest_id() {
		return request_id;
	}

	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}

}
