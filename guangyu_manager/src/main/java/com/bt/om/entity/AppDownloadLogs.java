package com.bt.om.entity;

import java.util.Date;

public class AppDownloadLogs {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column app_download_logs.id
	 * @mbg.generated  Fri Aug 03 13:19:44 CST 2018
	 */
	private Integer id;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column app_download_logs.device
	 * @mbg.generated  Fri Aug 03 13:19:44 CST 2018
	 */
	private String device;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column app_download_logs.ip
	 * @mbg.generated  Fri Aug 03 13:19:44 CST 2018
	 */
	private String ip;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column app_download_logs.download_time
	 * @mbg.generated  Fri Aug 03 13:19:44 CST 2018
	 */
	private Date downloadTime;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column app_download_logs.id
	 * @return  the value of app_download_logs.id
	 * @mbg.generated  Fri Aug 03 13:19:44 CST 2018
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column app_download_logs.id
	 * @param id  the value for app_download_logs.id
	 * @mbg.generated  Fri Aug 03 13:19:44 CST 2018
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column app_download_logs.device
	 * @return  the value of app_download_logs.device
	 * @mbg.generated  Fri Aug 03 13:19:44 CST 2018
	 */
	public String getDevice() {
		return device;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column app_download_logs.device
	 * @param device  the value for app_download_logs.device
	 * @mbg.generated  Fri Aug 03 13:19:44 CST 2018
	 */
	public void setDevice(String device) {
		this.device = device == null ? null : device.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column app_download_logs.ip
	 * @return  the value of app_download_logs.ip
	 * @mbg.generated  Fri Aug 03 13:19:44 CST 2018
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column app_download_logs.ip
	 * @param ip  the value for app_download_logs.ip
	 * @mbg.generated  Fri Aug 03 13:19:44 CST 2018
	 */
	public void setIp(String ip) {
		this.ip = ip == null ? null : ip.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column app_download_logs.download_time
	 * @return  the value of app_download_logs.download_time
	 * @mbg.generated  Fri Aug 03 13:19:44 CST 2018
	 */
	public Date getDownloadTime() {
		return downloadTime;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column app_download_logs.download_time
	 * @param downloadTime  the value for app_download_logs.download_time
	 * @mbg.generated  Fri Aug 03 13:19:44 CST 2018
	 */
	public void setDownloadTime(Date downloadTime) {
		this.downloadTime = downloadTime;
	}
}