package com.bt.om.mapper;

import java.util.List;
import java.util.Map;

import com.bt.om.entity.TkOrderInputJd;

public interface TkOrderInputJdMapper {
    /**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table tk_order_input_jd
	 * @mbg.generated  Fri May 04 19:35:01 CST 2018
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table tk_order_input_jd
	 * @mbg.generated  Fri May 04 19:35:01 CST 2018
	 */
	int insert(TkOrderInputJd record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table tk_order_input_jd
	 * @mbg.generated  Fri May 04 19:35:01 CST 2018
	 */
	int insertSelective(TkOrderInputJd record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table tk_order_input_jd
	 * @mbg.generated  Fri May 04 19:35:01 CST 2018
	 */
	TkOrderInputJd selectByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table tk_order_input_jd
	 * @mbg.generated  Fri May 04 19:35:01 CST 2018
	 */
	int updateByPrimaryKeySelective(TkOrderInputJd record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table tk_order_input_jd
	 * @mbg.generated  Fri May 04 19:35:01 CST 2018
	 */
	int updateByPrimaryKey(TkOrderInputJd record);

	List<TkOrderInputJd> selectByOrderId(String orderId);
    
    void truncateTkOrderInputJd();
    
    TkOrderInputJd selectByMap(Map<String,Object> map);
}