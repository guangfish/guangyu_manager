package com.bt.om.mapper;

import com.bt.om.entity.TaobaoPids;

public interface TaobaoPidsMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table taobao_pids
	 * @mbg.generated  Thu Dec 27 09:08:15 CST 2018
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table taobao_pids
	 * @mbg.generated  Thu Dec 27 09:08:15 CST 2018
	 */
	int insert(TaobaoPids record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table taobao_pids
	 * @mbg.generated  Thu Dec 27 09:08:15 CST 2018
	 */
	int insertSelective(TaobaoPids record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table taobao_pids
	 * @mbg.generated  Thu Dec 27 09:08:15 CST 2018
	 */
	TaobaoPids selectByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table taobao_pids
	 * @mbg.generated  Thu Dec 27 09:08:15 CST 2018
	 */
	int updateByPrimaryKeySelective(TaobaoPids record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table taobao_pids
	 * @mbg.generated  Thu Dec 27 09:08:15 CST 2018
	 */
	int updateByPrimaryKey(TaobaoPids record);
}