package com.bt.om.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.TkOrderInput;
import com.bt.om.mapper.TkOrderInputMapper;
import com.bt.om.service.ITkOrderInputService;

@Service
public class TkOrderInputService implements ITkOrderInputService {
	@Autowired
	private TkOrderInputMapper tkOrderInputMapper;

	@Override
	public List<TkOrderInput> selectByOrderId(String sign) {
		return tkOrderInputMapper.selectByOrderId(sign);
	}
	
	@Override
	public void insert(TkOrderInput tkOrderInput) {
		tkOrderInputMapper.insert(tkOrderInput);
	}
	
	@Override
	public void updateByOrderId(TkOrderInput record) {
		tkOrderInputMapper.updateByOrderId(record);
	}
	
	@Override
	public void truncateTkOrderInput() {
		tkOrderInputMapper.truncateTkOrderInput();
	}
}
