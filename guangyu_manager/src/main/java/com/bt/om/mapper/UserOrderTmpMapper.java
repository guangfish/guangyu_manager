package com.bt.om.mapper;

import java.util.List;

import com.bt.om.entity.UserOrderTmp;

public interface UserOrderTmpMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user_order_tmp
	 * @mbg.generated  Tue Nov 20 17:13:46 CST 2018
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user_order_tmp
	 * @mbg.generated  Tue Nov 20 17:13:46 CST 2018
	 */
	int insert(UserOrderTmp record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user_order_tmp
	 * @mbg.generated  Tue Nov 20 17:13:46 CST 2018
	 */
	int insertSelective(UserOrderTmp record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user_order_tmp
	 * @mbg.generated  Tue Nov 20 17:13:46 CST 2018
	 */
	UserOrderTmp selectByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user_order_tmp
	 * @mbg.generated  Tue Nov 20 17:13:46 CST 2018
	 */
	int updateByPrimaryKeySelective(UserOrderTmp record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user_order_tmp
	 * @mbg.generated  Tue Nov 20 17:13:46 CST 2018
	 */
	int updateByPrimaryKey(UserOrderTmp record);

	List<UserOrderTmp> selectUnCheckOrder(Integer belong);
	
	UserOrderTmp selectByOrderId(String orderId);
}