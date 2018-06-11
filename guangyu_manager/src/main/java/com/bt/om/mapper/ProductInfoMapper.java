package com.bt.om.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;

import com.bt.om.entity.ProductInfo;

public interface ProductInfoMapper {
    /**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table product_info
	 * @mbg.generated  Sun Jun 10 18:32:41 CST 2018
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table product_info
	 * @mbg.generated  Sun Jun 10 18:32:41 CST 2018
	 */
	int insert(ProductInfo record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table product_info
	 * @mbg.generated  Sun Jun 10 18:32:41 CST 2018
	 */
	int insertSelective(ProductInfo record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table product_info
	 * @mbg.generated  Sun Jun 10 18:32:41 CST 2018
	 */
	ProductInfo selectByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table product_info
	 * @mbg.generated  Sun Jun 10 18:32:41 CST 2018
	 */
	int updateByPrimaryKeySelective(ProductInfo record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table product_info
	 * @mbg.generated  Sun Jun 10 18:32:41 CST 2018
	 */
	int updateByPrimaryKey(ProductInfo record);

	ProductInfo selectByProductId(String productId);
    
    List<ProductInfo> selectByProductIds(Map<String,Object> productIdMap);
    
    List<ProductInfo> selectList(ProductInfo productInfo);
    
    void updateByProductId(ProductInfo productInfo);
    
    int selectProductInfoListCount(Map<String, Object> searchMap);

    List<ProductInfo> selectProductInfoList(Map<String, Object> searchMap, RowBounds rowBounds);
    
    List<ProductInfo> selectProductInfoListRand(Integer size);
    
    List<ProductInfo> selectAllList();
}