package com.bt.om.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;

import com.bt.om.entity.UserOrder;

public interface UserOrderMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user_order
	 * @mbg.generated  Mon Jul 02 11:40:03 CST 2018
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user_order
	 * @mbg.generated  Mon Jul 02 11:40:03 CST 2018
	 */
	int insert(UserOrder record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user_order
	 * @mbg.generated  Mon Jul 02 11:40:03 CST 2018
	 */
	int insertSelective(UserOrder record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user_order
	 * @mbg.generated  Mon Jul 02 11:40:03 CST 2018
	 */
	UserOrder selectByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user_order
	 * @mbg.generated  Mon Jul 02 11:40:03 CST 2018
	 */
	int updateByPrimaryKeySelective(UserOrder record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table user_order
	 * @mbg.generated  Mon Jul 02 11:40:03 CST 2018
	 */
	int updateByPrimaryKey(UserOrder record);

	void updateRewardStatus(UserOrder userOrder);

	List<UserOrder> selectByMobile(String mobile);
	
	List<UserOrder> selectAllOrderByMobile(String mobile);
	
	void updateStatus2(UserOrder userOrder);
	
	List<UserOrder> selectUnCheckOrderTaobao(UserOrder userOrder);
	
	List<UserOrder> selectUnCheckOrderJd(UserOrder userOrder);
	
	List<UserOrder> selectUnSuccessOrder(Map<String, Object> map);
	
	int getAllListCount(Map<String, Object> searchMap);
	
	List<Map<String, Object>> getAllList(Map<String, Object> searchMap, RowBounds rowBounds);
	
	List<UserOrder> findByMobile(String mobile);
	
	List<UserOrder> selectByInviteCode(String taInviteCode);
}