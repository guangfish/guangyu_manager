package com.bt.om.mapper;

import com.bt.om.entity.User;

public interface UserMapper {
    /**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user
	 * @mbg.generated  Thu Jul 12 13:03:50 CST 2018
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user
	 * @mbg.generated  Thu Jul 12 13:03:50 CST 2018
	 */
	int insert(User record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user
	 * @mbg.generated  Thu Jul 12 13:03:50 CST 2018
	 */
	int insertSelective(User record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user
	 * @mbg.generated  Thu Jul 12 13:03:50 CST 2018
	 */
	User selectByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user
	 * @mbg.generated  Thu Jul 12 13:03:50 CST 2018
	 */
	int updateByPrimaryKeySelective(User record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user
	 * @mbg.generated  Thu Jul 12 13:03:50 CST 2018
	 */
	int updateByPrimaryKey(User record);

	User selectByMobile(String mobile);
	
	User selectByTaInviteCode(String taInviteCode);
}