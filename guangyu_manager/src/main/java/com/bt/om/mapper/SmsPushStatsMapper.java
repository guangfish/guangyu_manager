package com.bt.om.mapper;

import com.bt.om.entity.SmsPushStats;

public interface SmsPushStatsMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sms_push_stats
     *
     * @mbg.generated Thu Nov 15 12:36:49 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sms_push_stats
     *
     * @mbg.generated Thu Nov 15 12:36:49 CST 2018
     */
    int insert(SmsPushStats record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sms_push_stats
     *
     * @mbg.generated Thu Nov 15 12:36:49 CST 2018
     */
    int insertSelective(SmsPushStats record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sms_push_stats
     *
     * @mbg.generated Thu Nov 15 12:36:49 CST 2018
     */
    SmsPushStats selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sms_push_stats
     *
     * @mbg.generated Thu Nov 15 12:36:49 CST 2018
     */
    int updateByPrimaryKeySelective(SmsPushStats record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sms_push_stats
     *
     * @mbg.generated Thu Nov 15 12:36:49 CST 2018
     */
    int updateByPrimaryKey(SmsPushStats record);
}