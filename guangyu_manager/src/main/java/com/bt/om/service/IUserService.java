package com.bt.om.service;

import java.util.List;

import com.bt.om.entity.User;

public interface IUserService {
	public User selectByMobile(String mobile);
	
	public List<User> selectByAlipay(String alipay);
	
	public void insert(User user);
	
	public User selectByTaInviteCode(String taInviteCode);
	
	public void updateHongbao(User user);
	
	public void update(User user);
}
