package com.bt.om.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.ProductInfo;
import com.bt.om.mapper.ProductInfoMapper;
import com.bt.om.service.IProductInfoService;
import com.bt.om.vo.web.SearchDataVo;

@Service
public class ProductInfoService implements IProductInfoService {
	@Autowired
	private ProductInfoMapper productInfoMapper;

	@Override
	public ProductInfo getByProductId(String productId) {
		return productInfoMapper.selectByProductId(productId);
	}

	@Override
	public List<ProductInfo> getByProductIds(Map<String, Object> productIdMap) {
		return productInfoMapper.selectByProductIds(productIdMap);
	}

	@Override
	public List<ProductInfo> getList(ProductInfo productInfo) {
		return productInfoMapper.selectList(productInfo);
	}
	
	@Override
	public void updateCommission(ProductInfo productInfo){
		productInfoMapper.updateByProductId(productInfo);
	}
	
	@Override
	public void insertProductInfo(ProductInfo productInfo){
		productInfoMapper.insert(productInfo);
	}
	
	@Override
	public void selectProductInfoList(SearchDataVo vo){
		int count = productInfoMapper.selectProductInfoListCount(vo.getSearchMap());
		vo.setCount(count);
		if (count > 0) {
			vo.setList(productInfoMapper.selectProductInfoList(vo.getSearchMap(), new RowBounds(vo.getStart(), vo.getSize())));
		} else {
			vo.setList(new ArrayList<ProductInfo>());
		}
	}
	
	@Override
	public List<ProductInfo> selectProductInfoListRand(Integer size) {
		return productInfoMapper.selectProductInfoListRand(size);
	}
	
	public List<ProductInfo> selectAllList(){
		return productInfoMapper.selectAllList();
	}
	
	public void deleteByPrimaryKey(Integer id){
		productInfoMapper.deleteByPrimaryKey(id);
	}
	
	public void updateByPrimaryKey(ProductInfo productInfo){
		productInfoMapper.updateByPrimaryKey(productInfo);
	}
}
