package com.bt.om.system;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.Resource;
import com.bt.om.service.IResourceService;
import com.bt.om.util.DateUtil;
import com.bt.om.util.NumberUtil;

/**
 * Created by chenhj on 2017/9/28.
 */
@Service
public class GlobalVariable {
	Logger logger = LoggerFactory.getLogger(GlobalVariable.class);
	public static Map<String, String> resourceMap = new HashMap<String, String>();

	@Autowired
	private IResourceService resourceService;

	public void init() {
		logger.info("初始化全局变量开始！~~~~~~~~~~~~~~~");
		// 这里可以做初始化操作
		loadResource();
		logger.info("初始化全局变量完成！~~~~~~~~~~~~~~~");
	}

	public void loadResource() {
		List<Resource> resourceList = resourceService.selectAll();
		if (resourceList != null && resourceList.size() > 0) {
			for (Resource resource : resourceList) {
				resourceMap.put(resource.getName(), resource.getValue());
			}
		}
        
		//定时增加提示的节约金额
		String date = DateUtil.dateFormate(new Date(), DateUtil.CHINESE_PATTERN);
		if (resourceMap.get(date) != null) {
			long maney = Long.parseLong(resourceMap.get(date));
			maney = maney + NumberUtil.getRandomInt(50, 100);
			resourceMap.put(date, maney + "");
		} else {
			resourceMap.put(date, "10");
		}
	}
}
