package com.bt.om.taobao.api;

import java.io.Serializable;

public class MaterialSearchVo implements Serializable {

	private static final long serialVersionUID = 6074092674747570646L;
	private MaterialResponse tbk_dg_material_optional_response;

	public MaterialResponse getTbk_dg_material_optional_response() {
		return tbk_dg_material_optional_response;
	}

	public void setTbk_dg_material_optional_response(MaterialResponse tbk_dg_material_optional_response) {
		this.tbk_dg_material_optional_response = tbk_dg_material_optional_response;
	}

}
