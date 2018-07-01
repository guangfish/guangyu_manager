package com.bt.om.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.UserOrderTmp;
import com.bt.om.mapper.UserOrderTmpMapper;
import com.bt.om.service.IUserOrderTmpService;

@Service
public class UserOrderTmpService implements IUserOrderTmpService {
	@Autowired
	private UserOrderTmpMapper userOrderTmpMapper;

	@Override
	public void insert(UserOrderTmp userOrderTmp) {
		userOrderTmpMapper.insert(userOrderTmp);
	}
	
	@Override
	public void update(UserOrderTmp userOrderTmp) {
		userOrderTmpMapper.updateByPrimaryKey(userOrderTmp);
	}
	
	public List<UserOrderTmp> selectUnCheckOrder(Integer belong){
		return userOrderTmpMapper.selectUnCheckOrder(belong);
	}
	
	public UserOrderTmp selectByOrderId(String orderId){
		return userOrderTmpMapper.selectByOrderId(orderId);
	}
}
