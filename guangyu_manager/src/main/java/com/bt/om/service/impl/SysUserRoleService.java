package com.bt.om.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.SysUserRole;
import com.bt.om.mapper.SysUserRoleMapper;
import com.bt.om.service.ISysUserRoleService;

@Service
public class SysUserRoleService implements ISysUserRoleService {
	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;

	@Override
	public void insert(SysUserRole sysUserRole) {
		sysUserRoleMapper.insert(sysUserRole);
	}
}
