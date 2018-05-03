package com.bt.om.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.TkInfoTask;
import com.bt.om.mapper.TkInfoTaskMapper;
import com.bt.om.service.ITkInfoTaskService;

@Service
public class TkInfoTaskService implements ITkInfoTaskService {
	@Autowired
	private TkInfoTaskMapper tkInfoTaskMapper;

	@Override
	public void insertTkInfoTask(TkInfoTask tkInfoTask) {
		tkInfoTaskMapper.insert(tkInfoTask);
	}

	@Override
	public TkInfoTask selectBySign(String sign) {
		return tkInfoTaskMapper.selectBySign(sign);
	}
}
