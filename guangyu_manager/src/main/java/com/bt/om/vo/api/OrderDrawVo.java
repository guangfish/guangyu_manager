package com.bt.om.vo.api;

import java.io.Serializable;

public class OrderDrawVo implements Serializable {

	private static final long serialVersionUID = 3690982281582596226L;
	private int productNums;
	private String fanli;
	private String money;
	private String reward;
	private String status;

	public OrderDrawVo(int productNums,String fanli, String money,String reward, String status) {
		this.productNums = productNums;
		this.fanli=fanli;
		this.money = money;
		this.reward = reward;
		this.status = status;
	}

	public int getProductNums() {
		return productNums;
	}

	public void setProductNums(int productNums) {
		this.productNums = productNums;
	}

	public String getFanli() {
		return fanli;
	}

	public void setFanli(String fanli) {
		this.fanli = fanli;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

}
