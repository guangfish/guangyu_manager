package com.bt.om.web.controller.xcx.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemVo implements Serializable{
	private static final long serialVersionUID = -1390815588353123246L;
	
	private long totalSize = 0;
	private long curPage=1;
	private long maxPage=1;
	private boolean hasNext=false;
	private String mall = "";
	private String tkl="";
	private List<Map<String, String>> items=new ArrayList<>();
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
	public boolean isHasNext() {
		return hasNext;
	}
	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}
	public String getMall() {
		return mall;
	}
	public void setMall(String mall) {
		this.mall = mall;
	}	
	public String getTkl() {
		return tkl;
	}
	public void setTkl(String tkl) {
		this.tkl = tkl;
	}
	public List<Map<String, String>> getItems() {
		return items;
	}
	public void setItems(List<Map<String, String>> items) {
		this.items = items;
	}
	
	

}
