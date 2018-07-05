package com.bt.om.task;

import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bt.om.entity.ProductInfo;
import com.bt.om.entity.ProductInfoMid;
import com.bt.om.service.IProductInfoMidService;
import com.bt.om.service.IProductInfoService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.HttpcomponentsUtil;

/**
 * 定时通过/api/productInfo产销商品信息
 * 
 * @author chenhj
 *
 */
//@Component
public class ProductInfoScheduleTask {
	private static final Logger logger = Logger.getLogger(ProductInfoScheduleTask.class);

	@Autowired
	private IProductInfoService productInfoService;

	@Scheduled(cron = "0 0/2 * * * ?")
	public void valid() {
		String ifRun = GlobalVariable.resourceMap.get("ProductInfoScheduleTask");
		if ("1".equals(ifRun)) {
			logger.info("定时通过/api/productInfo产销商品信息");
			List<ProductInfo> productInfoList = productInfoService.selectProductInfoListRand(1);
			if (productInfoList == null || productInfoList.size() <= 0) {
				return;
			} else {
				ProductInfo productInfo = productInfoList.get(0);
				String params = "{\"product_url\":\"" + productInfo.getProductInfoUrl() + "\"}";
				System.out.println(params);
				String retStr="";
				try {
					retStr=HttpcomponentsUtil.doPost(ConfigUtil.getString("crawl.task.send.domain.test") + "/api/productInfo",
							params);
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
