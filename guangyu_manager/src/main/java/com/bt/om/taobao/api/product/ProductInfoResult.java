package com.bt.om.taobao.api.product;

import java.io.Serializable;
import java.util.List;

public class ProductInfoResult implements Serializable {

	private static final long serialVersionUID = 3865955049617895381L;
	private List<ProductInfoItem> n_tbk_item;
	public List<ProductInfoItem> getN_tbk_item() {
		return n_tbk_item;
	}
	public void setN_tbk_item(List<ProductInfoItem> n_tbk_item) {
		this.n_tbk_item = n_tbk_item;
	}


}
