package com.bt.om.service;

import java.util.List;
import java.util.Map;

import com.bt.om.entity.TkOrderInputJd;

public interface ITkOrderInputJdService {
	public List<TkOrderInputJd> selectByOrderId(String orderId);
	
	public TkOrderInputJd selectByMap(Map<String,Object> map);

	public void insert(TkOrderInputJd tkOrderInputJd);

	public void truncateTkOrderInputJd();
}
