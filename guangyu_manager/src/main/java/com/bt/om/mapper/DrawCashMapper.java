package com.bt.om.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.bt.om.entity.DrawCash;
import com.bt.om.entity.ProductInfo;
import com.bt.om.entity.UserOrder;

public interface DrawCashMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table draw_cash
	 * @mbg.generated  Tue Jul 17 17:31:51 CST 2018
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table draw_cash
	 * @mbg.generated  Tue Jul 17 17:31:51 CST 2018
	 */
	int insert(DrawCash record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table draw_cash
	 * @mbg.generated  Tue Jul 17 17:31:51 CST 2018
	 */
	int insertSelective(DrawCash record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table draw_cash
	 * @mbg.generated  Tue Jul 17 17:31:51 CST 2018
	 */
	DrawCash selectByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table draw_cash
	 * @mbg.generated  Tue Jul 17 17:31:51 CST 2018
	 */
	int updateByPrimaryKeySelective(DrawCash record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table draw_cash
	 * @mbg.generated  Tue Jul 17 17:31:51 CST 2018
	 */
	int updateByPrimaryKey(DrawCash record);

	int getDrawListCount(Map<String, Object> searchMap);
	
	List<DrawCash> getDrawList(Map<String, Object> searchMap, RowBounds rowBounds);
	
	int getUserOrderCountByDrawId(@Param("id") Integer id);
	
	List<Map<String,Object>> getUserOrderByDrawId(@Param("id") Integer id, RowBounds rowBounds);
	
	int updateUserOrderStatus2AndStatus3(@Param("id") Integer id, @Param("sysDate") Date sysDate);
	
	double getSumByMobile(Map<String, Object> searchMap);
	
	int selectDrawCashListCount(Map<String, Object> searchMap);

    List<DrawCash> selectDrawCashList(Map<String, Object> searchMap, RowBounds rowBounds);
}