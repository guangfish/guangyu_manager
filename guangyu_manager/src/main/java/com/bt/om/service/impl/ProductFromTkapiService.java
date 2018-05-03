package com.bt.om.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.ProductFromTkapi;
import com.bt.om.mapper.ProductFromTkapiMapper;
import com.bt.om.service.IProductFromTkapiService;

@Service
public class ProductFromTkapiService implements IProductFromTkapiService {
	@Autowired
	private ProductFromTkapiMapper productFromTkapiMapper;

	@Override
	public void insert(ProductFromTkapi productFromTkapi) {
		productFromTkapiMapper.insert(productFromTkapi);
	}
}
