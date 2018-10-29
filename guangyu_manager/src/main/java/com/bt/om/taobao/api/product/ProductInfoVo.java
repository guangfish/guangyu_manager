package com.bt.om.taobao.api.product;

import java.io.Serializable;

public class ProductInfoVo implements Serializable {

	private static final long serialVersionUID = 7002297647635893255L;
	private ProductInfoResponse tbk_item_info_get_response;

	public ProductInfoResponse getTbk_item_info_get_response() {
		return tbk_item_info_get_response;
	}

	public void setTbk_item_info_get_response(ProductInfoResponse tbk_item_info_get_response) {
		this.tbk_item_info_get_response = tbk_item_info_get_response;
	}
}
