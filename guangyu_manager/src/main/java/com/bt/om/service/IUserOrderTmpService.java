package com.bt.om.service;

import java.util.List;

import com.bt.om.entity.UserOrderTmp;

public interface IUserOrderTmpService {
	public void insert(UserOrderTmp userOrderTmp);
	
	public void update(UserOrderTmp userOrderTmp);
	
	public List<UserOrderTmp> selectUnCheckOrder(Integer belong);

}
