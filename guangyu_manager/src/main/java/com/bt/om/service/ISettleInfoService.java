package com.bt.om.service;

import com.bt.om.entity.SettleInfo;
import com.bt.om.vo.web.SearchDataVo;

public interface ISettleInfoService {
	public void insert(SettleInfo settleInfo);
	
	public void selectSettleInfoList(SearchDataVo vo);

}
