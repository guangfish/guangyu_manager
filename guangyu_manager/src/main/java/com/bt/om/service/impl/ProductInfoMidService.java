package com.bt.om.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.ProductInfoMid;
import com.bt.om.mapper.ProductInfoMidMapper;
import com.bt.om.service.IProductInfoMidService;

@Service
public class ProductInfoMidService implements IProductInfoMidService {
	@Autowired
	private ProductInfoMidMapper productInfoMidMapper;

	public List<ProductInfoMid> selectAllList() {
		return productInfoMidMapper.selectAllList();
	}

	public void deleteByPrimaryKey(Integer id) {
		productInfoMidMapper.deleteByPrimaryKey(id);
	}

	public void updateByPrimaryKey(ProductInfoMid productInfoMid) {
		productInfoMidMapper.updateByPrimaryKey(productInfoMid);
	}
	
	public List<ProductInfoMid> selectByStatusList(Integer ifvalid) {
		return productInfoMidMapper.selectByStatusList(ifvalid);
	}
}
