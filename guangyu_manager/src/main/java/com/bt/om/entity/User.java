package com.bt.om.entity;

import java.util.Date;

public class User {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user.id
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	private Integer id;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user.mobile
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	private String mobile;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user.password
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	private String password;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user.alipay
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	private String alipay;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user.weixin
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	private String weixin;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user.ta_invite_code
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	private String taInviteCode;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user.my_invite_code
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	private String myInviteCode;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user.account_type
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	private Integer accountType;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user.create_time
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	private Date createTime;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column user.update_time
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	private Date updateTime;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user.id
	 * @return  the value of user.id
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user.id
	 * @param id  the value for user.id
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user.mobile
	 * @return  the value of user.mobile
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user.mobile
	 * @param mobile  the value for user.mobile
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile == null ? null : mobile.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user.password
	 * @return  the value of user.password
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user.password
	 * @param password  the value for user.password
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public void setPassword(String password) {
		this.password = password == null ? null : password.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user.alipay
	 * @return  the value of user.alipay
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public String getAlipay() {
		return alipay;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user.alipay
	 * @param alipay  the value for user.alipay
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public void setAlipay(String alipay) {
		this.alipay = alipay == null ? null : alipay.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user.weixin
	 * @return  the value of user.weixin
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public String getWeixin() {
		return weixin;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user.weixin
	 * @param weixin  the value for user.weixin
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public void setWeixin(String weixin) {
		this.weixin = weixin == null ? null : weixin.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user.ta_invite_code
	 * @return  the value of user.ta_invite_code
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public String getTaInviteCode() {
		return taInviteCode;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user.ta_invite_code
	 * @param taInviteCode  the value for user.ta_invite_code
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public void setTaInviteCode(String taInviteCode) {
		this.taInviteCode = taInviteCode == null ? null : taInviteCode.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user.my_invite_code
	 * @return  the value of user.my_invite_code
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public String getMyInviteCode() {
		return myInviteCode;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user.my_invite_code
	 * @param myInviteCode  the value for user.my_invite_code
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public void setMyInviteCode(String myInviteCode) {
		this.myInviteCode = myInviteCode == null ? null : myInviteCode.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user.account_type
	 * @return  the value of user.account_type
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public Integer getAccountType() {
		return accountType;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user.account_type
	 * @param accountType  the value for user.account_type
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user.create_time
	 * @return  the value of user.create_time
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user.create_time
	 * @param createTime  the value for user.create_time
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column user.update_time
	 * @return  the value of user.update_time
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column user.update_time
	 * @param updateTime  the value for user.update_time
	 * @mbg.generated  Fri Jun 15 18:23:17 CST 2018
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}