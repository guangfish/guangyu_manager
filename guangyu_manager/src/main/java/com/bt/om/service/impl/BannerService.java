package com.bt.om.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.Banner;
import com.bt.om.mapper.BannerMapper;
import com.bt.om.service.IBannerService;

@Service
public class BannerService implements IBannerService {
	@Autowired
	private BannerMapper bannerMapper;

	@Override
	public List<Banner> selectAll(Integer type) {
		return bannerMapper.selectAll(type);
	}
}
