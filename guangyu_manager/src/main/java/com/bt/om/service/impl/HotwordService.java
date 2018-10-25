package com.bt.om.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.Hotword;
import com.bt.om.mapper.HotwordMapper;
import com.bt.om.service.IHotwordService;

@Service
public class HotwordService implements IHotwordService {
	@Autowired
	private HotwordMapper hotwordMapper;

	@Override
	public List<Hotword> selectAll() {
		return hotwordMapper.selectAll();
	}
}
