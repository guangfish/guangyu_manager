package com.bt.om.web.controller.app.vo;

import java.util.HashMap;
import java.util.List;

import com.bt.om.web.controller.api.v2.vo.CommonVo;

public class ResultVo extends CommonVo {
	private long totalSize = 0;
	private long curPage=1;
	private long maxPage=1;

	private List<HashMap<String, String>> data;

	public List<HashMap<String, String>> getData() {
		return data;
	}

	public void setData(List<HashMap<String, String>> data) {
		this.data = data;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public long getCurPage() {
		return curPage;
	}

	public void setCurPage(long curPage) {
		this.curPage = curPage;
	}

	public long getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(long maxPage) {
		this.maxPage = maxPage;
	}

}
