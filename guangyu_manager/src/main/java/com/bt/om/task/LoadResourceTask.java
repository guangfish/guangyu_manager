package com.bt.om.task;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bt.om.system.GlobalVariable;

@Component
public class LoadResourceTask {
	private static final Logger logger = Logger.getLogger(LoadResourceTask.class);

	@Autowired
    private GlobalVariable globalVariable;
	
	@Scheduled(cron = "0 0/1 * * * ?")
	public void getTask() {
//		logger.info("定时加载资源表");
//		logger.info(GlobalVariable.resourceMap.get("task.info.check.num"));
		globalVariable.loadResource();
	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((LoadResourceTask) ctx.getBean("loadResourceTask")).getTask();
	}
}
