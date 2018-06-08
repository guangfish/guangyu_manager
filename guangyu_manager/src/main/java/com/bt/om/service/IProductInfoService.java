package com.bt.om.service;

import java.util.List;
import java.util.Map;

import com.bt.om.entity.ProductInfo;
import com.bt.om.vo.web.SearchDataVo;

public interface IProductInfoService {
    public ProductInfo getByProductId(String productId);
    
    public List<ProductInfo> getByProductIds(Map<String,Object> productIdMap);
    
    public List<ProductInfo> getList(ProductInfo productInfo);
    
    public void updateCommission(ProductInfo productInfo);
    
    public void insertProductInfo(ProductInfo productInfo);
    
    public void selectProductInfoList(SearchDataVo vo);
    
    public List<ProductInfo> selectProductInfoListRand(Integer size);
    
    public List<ProductInfo> selectAllList();
    
    public void deleteByPrimaryKey(Integer id);
    
    public void updateByPrimaryKey(ProductInfo productInfo);
}
