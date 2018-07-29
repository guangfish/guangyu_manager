package com.bt.om.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.User;
import com.bt.om.mapper.UserMapper;
import com.bt.om.service.IUserService;

@Service
public class UserService implements IUserService {
	@Autowired
	private UserMapper userMapper;

	@Override
	public User selectByMobile(String mobile) {
		return userMapper.selectByMobile(mobile);
	}

	@Override
	public void insert(User user) {
		userMapper.insert(user);
	}

	@Override
	public User selectByTaInviteCode(String taInviteCode) {
		return userMapper.selectByTaInviteCode(taInviteCode);
	}
	
	@Override
	public void updateHongbao(User user) {
		userMapper.updateHongbao(user);
	}
	
	@Override
	public void update(User user) {
		userMapper.updateByPrimaryKey(user);
	}
}
