package com.bt.om.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.TkOrderInputJd;
import com.bt.om.mapper.TkOrderInputJdMapper;
import com.bt.om.service.ITkOrderInputJdService;

@Service
public class TkOrderInputJdService implements ITkOrderInputJdService {
	@Autowired
	private TkOrderInputJdMapper tkOrderInputJdMapper;

	@Override
	public List<TkOrderInputJd> selectByOrderId(String sign) {
		return tkOrderInputJdMapper.selectByOrderId(sign);
	}
}
