package com.bt.om.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;

import com.bt.om.entity.DrawCash;
import com.bt.om.entity.SettleInfo;

public interface SettleInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settle_info
     *
     * @mbg.generated Sat Nov 17 21:53:08 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settle_info
     *
     * @mbg.generated Sat Nov 17 21:53:08 CST 2018
     */
    int insert(SettleInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settle_info
     *
     * @mbg.generated Sat Nov 17 21:53:08 CST 2018
     */
    int insertSelective(SettleInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settle_info
     *
     * @mbg.generated Sat Nov 17 21:53:08 CST 2018
     */
    SettleInfo selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settle_info
     *
     * @mbg.generated Sat Nov 17 21:53:08 CST 2018
     */
    int updateByPrimaryKeySelective(SettleInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settle_info
     *
     * @mbg.generated Sat Nov 17 21:53:08 CST 2018
     */
    int updateByPrimaryKey(SettleInfo record);
    
    int selectSettleInfoListCount(Map<String, Object> searchMap);

    List<SettleInfo> selectSettleInfoList(Map<String, Object> searchMap, RowBounds rowBounds);
}