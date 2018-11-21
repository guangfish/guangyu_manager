package com.bt.om.service.impl;

import java.util.ArrayList;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.SettleInfo;
import com.bt.om.mapper.SettleInfoMapper;
import com.bt.om.service.ISettleInfoService;
import com.bt.om.vo.web.SearchDataVo;

@Service
public class SettleInfoService implements ISettleInfoService {
	@Autowired
	private SettleInfoMapper settleInfoMapper;

	@Override
	public void insert(SettleInfo settleInfo) {
		settleInfoMapper.insert(settleInfo);
	}
	
	@Override
	public void selectSettleInfoList(SearchDataVo vo){
		int count = settleInfoMapper.selectSettleInfoListCount(vo.getSearchMap());
		vo.setCount(count);
		if (count > 0) {
			vo.setList(settleInfoMapper.selectSettleInfoList(vo.getSearchMap(), new RowBounds(vo.getStart(), vo.getSize())));
		} else {
			vo.setList(new ArrayList<SettleInfo>());
		}
	}
}
