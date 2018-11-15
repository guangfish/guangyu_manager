package com.bt.om.task;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bt.om.cache.JedisPool;
import com.bt.om.web.controller.app.vo.ProductInfoVo;
import com.bt.om.web.controller.util.ProductSearchUtil;

@Component
public class ProductSearchTask {
	private static final Logger logger = Logger.getLogger(ProductSearchTask.class);

	@Autowired
	private JedisPool jedisPool;

	@Scheduled(cron = "0 30 21 * * ?")
	public void productSearch() {
		logger.info("根据keys定时查询商品信息");
		String[] keys = { "", "女装", "男装", "母婴", "数码", "洗护", "彩妆", "家居", "食品", "箱包", "家电", "百货", "运动", "配饰", "鞋子", "萌宠",
				"汽车用品" };
		int pages = 30;
		try {
			for (String key : keys) {
				logger.info("product key=" + key);
				for (int i = 1; i <= pages; i++) {
					logger.info("product key page=" + key + "_" + i);
					ProductInfoVo productInfoVo = ProductSearchUtil.productInfoApi(jedisPool,"",key, i, 30);
					if (productInfoVo != null) {
						jedisPool.putInCache("productSearch", key + "_" + i, productInfoVo, 24 * 60 * 60);
					}else{
						logger.info("product key page=" + key + "_" + i+" null");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((ProductSearchTask) ctx.getBean("productSearchTask")).productSearch();
	}
}
