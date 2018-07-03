package com.bt.om.taobao.api.coupon;

public class CouponResponse {
	private CouponResultList results;
	private Long total_results;

	public CouponResultList getResults() {
		return results;
	}

	public void setResults(CouponResultList results) {
		this.results = results;
	}

	public Long getTotal_results() {
		return total_results;
	}

	public void setTotal_results(Long total_results) {
		this.total_results = total_results;
	}

}
