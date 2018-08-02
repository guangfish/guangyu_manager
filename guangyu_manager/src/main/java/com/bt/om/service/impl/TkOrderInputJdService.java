package com.bt.om.service.impl;

import java.util.List;
import java.util.Map;

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

	@Override
	public TkOrderInputJd selectByMap(Map<String, Object> map) {
		return tkOrderInputJdMapper.selectByMap(map);
	}

	@Override
	public void insert(TkOrderInputJd tkOrderInputJd) {
		tkOrderInputJdMapper.insert(tkOrderInputJd);
	}

	@Override
	public void truncateTkOrderInputJd() {
		tkOrderInputJdMapper.truncateTkOrderInputJd();
	}

	@Override
	public void deleteByAccount(String account) {
		tkOrderInputJdMapper.deleteByAccount(account);
	}
}
