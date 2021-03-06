package com.bt.om.mapper;

import java.util.List;
import java.util.Map;

import com.bt.om.entity.TkOrderInput;

public interface TkOrderInputMapper {
    /**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table tk_order_input
	 * @mbg.generated  Thu Aug 02 08:35:15 CST 2018
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table tk_order_input
	 * @mbg.generated  Thu Aug 02 08:35:15 CST 2018
	 */
	int insert(TkOrderInput record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table tk_order_input
	 * @mbg.generated  Thu Aug 02 08:35:15 CST 2018
	 */
	int insertSelective(TkOrderInput record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table tk_order_input
	 * @mbg.generated  Thu Aug 02 08:35:15 CST 2018
	 */
	TkOrderInput selectByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table tk_order_input
	 * @mbg.generated  Thu Aug 02 08:35:15 CST 2018
	 */
	int updateByPrimaryKeySelective(TkOrderInput record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table tk_order_input
	 * @mbg.generated  Thu Aug 02 08:35:15 CST 2018
	 */
	int updateByPrimaryKey(TkOrderInput record);

	List<TkOrderInput> selectByOrderId(String orderId);
	
	void updateByOrderId(TkOrderInput record);
	
	void truncateTkOrderInput();
	
	List<TkOrderInput> selectByMap(Map<String,Object> map);
	
	void deleteByAccount(String account);
	
	List<TkOrderInput> selectAll();
}