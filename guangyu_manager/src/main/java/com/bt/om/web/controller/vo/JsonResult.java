package com.bt.om.web.controller.vo;

import java.io.Serializable;
import java.util.List;

public class JsonResult implements Serializable {
	private static final long serialVersionUID = 3159905137691700698L;
	private List<?> list;
	private Integer curPage;
	private Long tolrow;
	private Long maxPage;

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}

	public Integer getCurPage() {
		return curPage;
	}

	public void setCurPage(Integer curPage) {
		this.curPage = curPage;
	}

	public Long getTolrow() {
		return tolrow;
	}

	public void setTolrow(Long tolrow) {
		this.tolrow = tolrow;
	}

	public Long getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(Long maxPage) {
		this.maxPage = maxPage;
	}

}
