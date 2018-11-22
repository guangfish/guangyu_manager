package com.bt.om.taobao.api;

public class SearchVo {
	// 搜索关键词
	private String key = "";
	// 搜索的分类
	private String cat = "";
	// 淘宝广告位ID
	private String pid = "";
	// 搜索页
	private long page = 0;
	// 页大小
	private long size = 20;
	// 是否天猫 0:所有 1：淘宝 2：天猫
	private int ifTmall = 0;
	// 是否有优惠券 0：所有 1：有优惠券 2：无优惠券
	private int hasCoupon = 0;
	// 商品排序
	private String sort = "total_sales";

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCat() {
		return cat;
	}

	public void setCat(String cat) {
		this.cat = cat;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public long getPage() {
		return page;
	}

	public void setPage(long page) {
		this.page = page;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public int getIfTmall() {
		return ifTmall;
	}

	public void setIfTmall(int ifTmall) {
		this.ifTmall = ifTmall;
	}

	public int getHasCoupon() {
		return hasCoupon;
	}

	public void setHasCoupon(int hasCoupon) {
		this.hasCoupon = hasCoupon;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

}
