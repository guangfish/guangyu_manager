package com.bt.om.service;

import com.bt.om.entity.User;

public interface IUserService {
	public User selectByMobile(String mobile);
	
	public void insert(User user);
	
	public User selectByTaInviteCode(String taInviteCode);
	
	public void updateHongbao(User user);
	
	public void update(User user);
}
