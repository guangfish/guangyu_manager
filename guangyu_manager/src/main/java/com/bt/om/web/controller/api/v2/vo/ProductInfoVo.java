package com.bt.om.web.controller.api.v2.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductInfoVo extends CommonVo {
	private long totalSize = 0;
	private long curPage=1;
	private long maxPage=1;
	private boolean ifHasNextPage=false;
	private String mall = "";
	private List<Map<String, String>> data=new ArrayList<>();

	public List<Map<String, String>> getData() {
		return data;
	}

	public void setData(List<Map<String, String>> data) {
		this.data = data;
	}

	public String getMall() {
		return mall;
	}

	public void setMall(String mall) {
		this.mall = mall;
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

	public boolean isIfHasNextPage() {
		return ifHasNextPage;
	}

	public void setIfHasNextPage(boolean ifHasNextPage) {
		this.ifHasNextPage = ifHasNextPage;
	}

}
