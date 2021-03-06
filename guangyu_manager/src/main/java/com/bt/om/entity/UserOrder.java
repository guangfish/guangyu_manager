package com.bt.om.entity;

import java.util.Date;

public class UserOrder{

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.id
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Integer id;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.mobile
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private String mobile;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.alipay_account
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private String alipayAccount;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.user_id
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Integer userId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.order_time
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Date orderTime;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.product_num
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Integer productNum;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.order_id
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private String orderId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.price
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Double price;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.rate
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Double rate;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.belong
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Integer belong;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.product_id
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private String productId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.product_info
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private String productInfo;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.product_img_url
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private String productImgUrl;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.shop_name
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private String shopName;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.commission1
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Double commission1;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.commission2
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Double commission2;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.commission3
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Double commission3;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.fanli_multiple
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Float fanliMultiple;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.order_status
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private String orderStatus;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.status1
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Integer status1;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.status2
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Integer status2;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.status3
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Integer status3;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.commission_reward
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Double commissionReward;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.commission_reward_rate
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Integer commissionRewardRate;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.reward_status
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Integer rewardStatus;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.settle_status
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Integer settleStatus;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.create_time
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Date createTime;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user_order.update_time
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	private Date updateTime;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.id
	 * @return  the value of user_order.id
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.id
	 * @param id  the value for user_order.id
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.mobile
	 * @return  the value of user_order.mobile
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.mobile
	 * @param mobile  the value for user_order.mobile
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile == null ? null : mobile.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.alipay_account
	 * @return  the value of user_order.alipay_account
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public String getAlipayAccount() {
		return alipayAccount;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.alipay_account
	 * @param alipayAccount  the value for user_order.alipay_account
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setAlipayAccount(String alipayAccount) {
		this.alipayAccount = alipayAccount == null ? null : alipayAccount.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.user_id
	 * @return  the value of user_order.user_id
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.user_id
	 * @param userId  the value for user_order.user_id
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.order_time
	 * @return  the value of user_order.order_time
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Date getOrderTime() {
		return orderTime;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.order_time
	 * @param orderTime  the value for user_order.order_time
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.product_num
	 * @return  the value of user_order.product_num
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Integer getProductNum() {
		return productNum;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.product_num
	 * @param productNum  the value for user_order.product_num
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setProductNum(Integer productNum) {
		this.productNum = productNum;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.order_id
	 * @return  the value of user_order.order_id
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public String getOrderId() {
		return orderId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.order_id
	 * @param orderId  the value for user_order.order_id
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId == null ? null : orderId.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.price
	 * @return  the value of user_order.price
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.price
	 * @param price  the value for user_order.price
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setPrice(Double price) {
		this.price = price;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.rate
	 * @return  the value of user_order.rate
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Double getRate() {
		return rate;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.rate
	 * @param rate  the value for user_order.rate
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setRate(Double rate) {
		this.rate = rate;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.belong
	 * @return  the value of user_order.belong
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Integer getBelong() {
		return belong;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.belong
	 * @param belong  the value for user_order.belong
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setBelong(Integer belong) {
		this.belong = belong;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.product_id
	 * @return  the value of user_order.product_id
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.product_id
	 * @param productId  the value for user_order.product_id
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setProductId(String productId) {
		this.productId = productId == null ? null : productId.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.product_info
	 * @return  the value of user_order.product_info
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public String getProductInfo() {
		return productInfo;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.product_info
	 * @param productInfo  the value for user_order.product_info
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setProductInfo(String productInfo) {
		this.productInfo = productInfo == null ? null : productInfo.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.product_img_url
	 * @return  the value of user_order.product_img_url
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public String getProductImgUrl() {
		return productImgUrl;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.product_img_url
	 * @param productImgUrl  the value for user_order.product_img_url
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setProductImgUrl(String productImgUrl) {
		this.productImgUrl = productImgUrl == null ? null : productImgUrl.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.shop_name
	 * @return  the value of user_order.shop_name
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public String getShopName() {
		return shopName;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.shop_name
	 * @param shopName  the value for user_order.shop_name
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setShopName(String shopName) {
		this.shopName = shopName == null ? null : shopName.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.commission1
	 * @return  the value of user_order.commission1
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Double getCommission1() {
		return commission1;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.commission1
	 * @param commission1  the value for user_order.commission1
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setCommission1(Double commission1) {
		this.commission1 = commission1;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.commission2
	 * @return  the value of user_order.commission2
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Double getCommission2() {
		return commission2;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.commission2
	 * @param commission2  the value for user_order.commission2
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setCommission2(Double commission2) {
		this.commission2 = commission2;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.commission3
	 * @return  the value of user_order.commission3
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Double getCommission3() {
		return commission3;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.commission3
	 * @param commission3  the value for user_order.commission3
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setCommission3(Double commission3) {
		this.commission3 = commission3;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.fanli_multiple
	 * @return  the value of user_order.fanli_multiple
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Float getFanliMultiple() {
		return fanliMultiple;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.fanli_multiple
	 * @param fanliMultiple  the value for user_order.fanli_multiple
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setFanliMultiple(Float fanliMultiple) {
		this.fanliMultiple = fanliMultiple;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.order_status
	 * @return  the value of user_order.order_status
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public String getOrderStatus() {
		return orderStatus;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.order_status
	 * @param orderStatus  the value for user_order.order_status
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus == null ? null : orderStatus.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.status1
	 * @return  the value of user_order.status1
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Integer getStatus1() {
		return status1;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.status1
	 * @param status1  the value for user_order.status1
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setStatus1(Integer status1) {
		this.status1 = status1;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.status2
	 * @return  the value of user_order.status2
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Integer getStatus2() {
		return status2;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.status2
	 * @param status2  the value for user_order.status2
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setStatus2(Integer status2) {
		this.status2 = status2;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.status3
	 * @return  the value of user_order.status3
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Integer getStatus3() {
		return status3;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.status3
	 * @param status3  the value for user_order.status3
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setStatus3(Integer status3) {
		this.status3 = status3;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.commission_reward
	 * @return  the value of user_order.commission_reward
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Double getCommissionReward() {
		return commissionReward;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.commission_reward
	 * @param commissionReward  the value for user_order.commission_reward
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setCommissionReward(Double commissionReward) {
		this.commissionReward = commissionReward;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.commission_reward_rate
	 * @return  the value of user_order.commission_reward_rate
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Integer getCommissionRewardRate() {
		return commissionRewardRate;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.commission_reward_rate
	 * @param commissionRewardRate  the value for user_order.commission_reward_rate
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setCommissionRewardRate(Integer commissionRewardRate) {
		this.commissionRewardRate = commissionRewardRate;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.reward_status
	 * @return  the value of user_order.reward_status
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Integer getRewardStatus() {
		return rewardStatus;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.reward_status
	 * @param rewardStatus  the value for user_order.reward_status
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setRewardStatus(Integer rewardStatus) {
		this.rewardStatus = rewardStatus;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.settle_status
	 * @return  the value of user_order.settle_status
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Integer getSettleStatus() {
		return settleStatus;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.settle_status
	 * @param settleStatus  the value for user_order.settle_status
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setSettleStatus(Integer settleStatus) {
		this.settleStatus = settleStatus;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.create_time
	 * @return  the value of user_order.create_time
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.create_time
	 * @param createTime  the value for user_order.create_time
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user_order.update_time
	 * @return  the value of user_order.update_time
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user_order.update_time
	 * @param updateTime  the value for user_order.update_time
	 * @mbg.generated  Wed Nov 28 11:39:50 CST 2018
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}