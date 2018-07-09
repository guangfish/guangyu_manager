package com.bt.om.task;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bt.om.entity.ProductInfo;
import com.bt.om.service.IProductInfoService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.HttpcomponentsUtil;
import com.bt.om.util.NumberUtil;

/**
 * 定时通过/api/productInfo产销商品信息
 * 
 * @author chenhj
 *
 */
@Component
public class ProductInfoScheduleTask {
	private static final Logger logger = Logger.getLogger(ProductInfoScheduleTask.class);

	@Autowired
	private IProductInfoService productInfoService;

	private static String domain = ConfigUtil.getString("crawl.task.send.domain");
	static {
		if ("on".equals(ConfigUtil.getString("is_test_evn"))) {
			domain = ConfigUtil.getString("crawl.task.send.domain.test");
		}
	}

	@Scheduled(cron = "0 0/3 * * * ?")
//	@Scheduled(cron = "0/30 * * * * ?")
	public void valid() {
		String ifRun = GlobalVariable.resourceMap.get("ProductInfoScheduleTask");
		if ("1".equals(ifRun)) {
			logger.info("定时通过/api/productInfo查询商品信息");
			List<ProductInfo> productInfoList = productInfoService.selectProductInfoListRand(1);
			if (productInfoList == null || productInfoList.size() <= 0) {
				return;
			} else {
				ProductInfo productInfo = productInfoList.get(0);
				String params = "{\"product_url\":\"" + productInfo.getProductInfoUrl() + "\"}";
				System.out.println(params);
				String retStr = "";
				try {
					Thread.sleep(NumberUtil.getRandomNumber(60000, 120000));
					retStr = HttpcomponentsUtil.doPost(domain + "/api/productInfo", params);
					System.out.println(retStr);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((ProductInfoScheduleTask) ctx.getBean("productInfoScheduleTask")).valid();
	}
}
