package com.bt.om.service;

import java.util.List;

import com.bt.om.entity.Banner;

public interface IBannerService {
	public List<Banner> selectAll(Integer type);
	
	public List<Banner> selectForApp(Integer type);
	
	public List<Banner> selectCampaign(Integer size);
	
	public List<Banner> selectForXcx(Integer type);
}
