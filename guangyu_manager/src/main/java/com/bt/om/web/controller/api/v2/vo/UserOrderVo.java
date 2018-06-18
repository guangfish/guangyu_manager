package com.bt.om.web.controller.api.v2.vo;

import java.util.HashMap;
import java.util.List;

public class UserOrderVo extends CommonVo {
	private String canDraw;
	private int canDrawOrderNum = 0;
	private int uncanDrawOrderNum = 0;
	private float totalCommission = 0;
	private int friendNum = 0;
	private int friendNumValid = 0;
	private int friendNumNoValid = 0;
	private int reward = 0;
	private int rewardAll = 0;
	private List<HashMap<String, String>> map;

	public String getCanDraw() {
		return canDraw;
	}

	public void setCanDraw(String canDraw) {
		this.canDraw = canDraw;
	}

	public int getCanDrawOrderNum() {
		return canDrawOrderNum;
	}

	public void setCanDrawOrderNum(int canDrawOrderNum) {
		this.canDrawOrderNum = canDrawOrderNum;
	}

	public int getUncanDrawOrderNum() {
		return uncanDrawOrderNum;
	}

	public void setUncanDrawOrderNum(int uncanDrawOrderNum) {
		this.uncanDrawOrderNum = uncanDrawOrderNum;
	}

	public float getTotalCommission() {
		return totalCommission;
	}

	public void setTotalCommission(float totalCommission) {
		this.totalCommission = totalCommission;
	}

	public int getFriendNum() {
		return friendNum;
	}

	public void setFriendNum(int friendNum) {
		this.friendNum = friendNum;
	}

	public int getFriendNumValid() {
		return friendNumValid;
	}

	public void setFriendNumValid(int friendNumValid) {
		this.friendNumValid = friendNumValid;
	}

	public int getFriendNumNoValid() {
		return friendNumNoValid;
	}

	public void setFriendNumNoValid(int friendNumNoValid) {
		this.friendNumNoValid = friendNumNoValid;
	}

	public int getReward() {
		return reward;
	}

	public void setReward(int reward) {
		this.reward = reward;
	}

	public int getRewardAll() {
		return rewardAll;
	}

	public void setRewardAll(int rewardAll) {
		this.rewardAll = rewardAll;
	}

	public List<HashMap<String, String>> getMap() {
		return map;
	}

	public void setMap(List<HashMap<String, String>> map) {
		this.map = map;
	}

}
