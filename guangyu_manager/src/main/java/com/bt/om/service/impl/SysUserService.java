package com.bt.om.service.impl;

import com.bt.om.entity.SysUser;
import com.bt.om.entity.vo.SysUserVo;
import com.bt.om.mapper.SysUserMapper;
import com.bt.om.mapper.SysUserRoleMapper;
import com.bt.om.service.ISysUserService;
import com.bt.om.vo.web.SearchDataVo;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

//import com.bt.om.mapper.OttvUserinfoManagerMapper;

/**
 *
 * OttvUser表数据服务层接口实现类
 *
 */
@Service
public class SysUserService implements ISysUserService {


	@Autowired
	SysUserMapper sysUserMapper;
	@Autowired
	SysUserRoleMapper sysUserRoleMapper;
//	@Autowired
//	OttvUserinfoManagerMapper ottvUserinfoManagerMapper;
	
	/*
	 * (non-Javadoc)
	 * @see com.bt.om.service.IOttvUserService#findUserinfoById(java.lang.Integer)
	 */
	@Override
	public SysUserVo findUserinfoById(Integer id) {
		return sysUserMapper.findUserinfoById(id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.bt.om.service.IOttvUserService#findByUsername(java.lang.String)
	 */
	@Override
	public SysUserVo findByUsername(String username) {
		return sysUserMapper.findByUsername(username);
	}

	/*
	 * (non-Javadoc)
	 * @see com.bt.om.service.IOttvUserService#getPageCount(java.util.Map)
	 */
	@Override
	public int getPageCount(Map<String, Object> searchMap) {
		return sysUserMapper.getPageCount(searchMap);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.bt.om.service.IOttvUserService#getPageData(com.bt.om.vo.web.SearchDataVo)
	 */
	@Override
	public List<SysUserVo> getPageData(SearchDataVo vo) {
		return sysUserMapper.getPageData(vo.getSearchMap(), new RowBounds(vo.getStart(), vo.getSize()));
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.bt.om.service.IOttvUserService#isExistsName(java.lang.String)
	 */
	@Override
	public List<SysUser> isExistsName(String username) {
		return sysUserMapper.isExistsName(username);
	}
	
	public void insert(SysUser sysUser) {
		sysUserMapper.insert(sysUser);
	}
}