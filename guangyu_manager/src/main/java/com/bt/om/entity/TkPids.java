package com.bt.om.entity;

import java.util.Date;

public class TkPids {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tk_pids.id
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tk_pids.create_time
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tk_pids.customer_id
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    private String customerId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tk_pids.pid
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    private String pid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tk_pids.pid_name
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    private String pidName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tk_pids.tk_id
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    private String tkId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tk_pids.id
     *
     * @return the value of tk_pids.id
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tk_pids.id
     *
     * @param id the value for tk_pids.id
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tk_pids.create_time
     *
     * @return the value of tk_pids.create_time
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tk_pids.create_time
     *
     * @param createTime the value for tk_pids.create_time
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tk_pids.customer_id
     *
     * @return the value of tk_pids.customer_id
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tk_pids.customer_id
     *
     * @param customerId the value for tk_pids.customer_id
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId == null ? null : customerId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tk_pids.pid
     *
     * @return the value of tk_pids.pid
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    public String getPid() {
        return pid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tk_pids.pid
     *
     * @param pid the value for tk_pids.pid
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    public void setPid(String pid) {
        this.pid = pid == null ? null : pid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tk_pids.pid_name
     *
     * @return the value of tk_pids.pid_name
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    public String getPidName() {
        return pidName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tk_pids.pid_name
     *
     * @param pidName the value for tk_pids.pid_name
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    public void setPidName(String pidName) {
        this.pidName = pidName == null ? null : pidName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tk_pids.tk_id
     *
     * @return the value of tk_pids.tk_id
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    public String getTkId() {
        return tkId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tk_pids.tk_id
     *
     * @param tkId the value for tk_pids.tk_id
     *
     * @mbg.generated Wed Dec 26 14:50:36 CST 2018
     */
    public void setTkId(String tkId) {
        this.tkId = tkId == null ? null : tkId.trim();
    }
}