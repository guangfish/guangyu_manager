package com.bt.om.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.Resource;
import com.bt.om.mapper.ResourceMapper;
import com.bt.om.service.IResourceService;

@Service
public class ResourceService implements IResourceService {
	@Autowired
	private ResourceMapper resourceMapper;

	@Override
	public List<Resource> selectAll() {
		return resourceMapper.selectAll();
	}
}
