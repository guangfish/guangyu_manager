package com.bt.om.task;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bt.om.entity.ProductInfo;
import com.bt.om.service.IProductInfoService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.HttpcomponentsUtil;

@Component
public class ProductInfoValidTask {
	private static final Logger logger = Logger.getLogger(ProductInfoValidTask.class);
	@Autowired
	private IProductInfoService productInfoService;

	@Scheduled(cron = "0/30 * * * * ?")
	public void valid() {
		String ifRun = GlobalVariable.resourceMap.get("ProductInfoValidTask");
		if ("1".equals(ifRun)) {
			logger.info("定时验证商品信息是否完整");
			List<ProductInfo> productInfoList = productInfoService.selectAllList();
			if (productInfoList == null || productInfoList.size() <= 0) {
				return;
			}

			for (ProductInfo productInfo : productInfoList) {
				try {
					String ret = HttpcomponentsUtil.getReq(productInfo.getProductImgUrl());
					if ("false".equals(ret)) {
						productInfoService.deleteByPrimaryKey(productInfo.getId());
					} else {
						productInfo.setIfvalid(2);
						Pattern p = Pattern.compile("减(\\d+)元");
						Matcher m = p.matcher(productInfo.getCouponMiane());
						if (m.find()) {
							productInfo.setCouponQuan(m.group(1));
						}
						p = Pattern.compile("(\\d+)元无条件券");
						m = p.matcher(productInfo.getCouponMiane());
						if (m.find()) {
							productInfo.setCouponQuan(m.group(1));
						}
						productInfoService.updateByPrimaryKey(productInfo);
					}
				} catch (Exception e) {
					e.printStackTrace();
					productInfoService.deleteByPrimaryKey(productInfo.getId());
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((ProductInfoValidTask) ctx.getBean("productInfoValidTask")).valid();
	}
}
