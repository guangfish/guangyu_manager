package com.bt.om.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.Resource;
import com.bt.om.service.IResourceService;

/**
 * Created by chenhj on 2017/9/28.
 */
@Service
public class GlobalVariable {
	Logger logger = LoggerFactory.getLogger(GlobalVariable.class);
	public static Map<String,String> resourceMap=new HashMap<String,String>();
	
	@Autowired
	private IResourceService resourceService;

	public void init() {
		logger.info("初始化全局变量开始！~~~~~~~~~~~~~~~");
		// 这里可以做初始化操作
		loadResource();
		logger.info("初始化全局变量完成！~~~~~~~~~~~~~~~");
	}
	
	public void loadResource(){
		List<Resource> resourceList = resourceService.selectAll();
		if(resourceList!=null && resourceList.size()>0){
			for(Resource resource:resourceList){
				resourceMap.put(resource.getName(), resource.getValue());
			}
		}
	}
}
