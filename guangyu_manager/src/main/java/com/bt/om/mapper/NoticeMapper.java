package com.bt.om.mapper;

import java.util.List;

import com.bt.om.entity.Notice;

public interface NoticeMapper {
    /**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table notice
	 * @mbg.generated  Tue May 29 09:17:58 CST 2018
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table notice
	 * @mbg.generated  Tue May 29 09:17:58 CST 2018
	 */
	int insert(Notice record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table notice
	 * @mbg.generated  Tue May 29 09:17:58 CST 2018
	 */
	int insertSelective(Notice record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table notice
	 * @mbg.generated  Tue May 29 09:17:58 CST 2018
	 */
	Notice selectByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table notice
	 * @mbg.generated  Tue May 29 09:17:58 CST 2018
	 */
	int updateByPrimaryKeySelective(Notice record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table notice
	 * @mbg.generated  Tue May 29 09:17:58 CST 2018
	 */
	int updateByPrimaryKey(Notice record);

	List<Notice> selectAll();
}