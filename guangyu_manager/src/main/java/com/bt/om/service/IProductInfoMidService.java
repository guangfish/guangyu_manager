package com.bt.om.service;

import java.util.List;

import com.bt.om.entity.ProductInfoMid;

public interface IProductInfoMidService {

	public List<ProductInfoMid> selectAllList();

	public void deleteByPrimaryKey(Integer id);

	public void updateByPrimaryKey(ProductInfoMid productInfoMid);
	
	public List<ProductInfoMid> selectByStatusList(Integer ifvalid);
}
