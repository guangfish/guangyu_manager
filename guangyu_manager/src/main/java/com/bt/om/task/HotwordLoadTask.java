package com.bt.om.task;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bt.om.cache.JedisPool;
import com.bt.om.entity.Hotword;
import com.bt.om.service.IHotwordService;

@Component
public class HotwordLoadTask {
	private static final Logger logger = Logger.getLogger(HotwordLoadTask.class);
	@Autowired
	private IHotwordService hotwordService;
	@Autowired
	private JedisPool jedisPool;
	
	@Scheduled(cron = "0 0 0/1 * * ?")
	public void getTask() {
		logger.info("热搜词定时加载");
		List<Hotword> hotwordList = hotwordService.selectAll();
		jedisPool.putNoTimeInCache("", "hotword", hotwordList);
	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((HotwordLoadTask) ctx.getBean("hotwordLoadTask")).getTask();
	}
}
