package com.bt.om.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.DrawCashOrder;
import com.bt.om.mapper.DrawCashOrderMapper;
import com.bt.om.service.IDrawCashOrderService;

@Service
public class DrawCashOrderService implements IDrawCashOrderService {
	@Autowired
	private DrawCashOrderMapper drawCashOrderMapper;

	@Override
	public void insert(DrawCashOrder drawCashOrder) {
		drawCashOrderMapper.insert(drawCashOrder);
	}
}
