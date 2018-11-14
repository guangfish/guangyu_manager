package com.bt.om.web.controller.xcx.util;

import com.bt.om.web.controller.api.v2.vo.CommonVo;

public class ProductInfoVo extends CommonVo implements Cloneable{

	private ItemVo data;

	public ItemVo getData() {
		return data;
	}

	public void setData(ItemVo data) {
		this.data = data;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
