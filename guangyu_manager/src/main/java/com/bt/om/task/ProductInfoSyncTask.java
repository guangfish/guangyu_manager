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

@Component
public class ProductInfoSyncTask {
	private static final Logger logger = Logger.getLogger(ProductInfoSyncTask.class);
	@Autowired
	private IProductInfoMidService productInfoMidService;

	@Autowired
	private IProductInfoService productInfoService;

	@Scheduled(cron = "0 0/5 * * * ?")
	public void valid() {
		String ifRun = GlobalVariable.resourceMap.get("ProductInfoSyncTask");
		if ("1".equals(ifRun)) {
			logger.info("开启商品信息product_info_mid表同步到product_info表");
			List<ProductInfoMid> productInfoList = productInfoMidService.selectByStatusList(2);
			if (productInfoList == null || productInfoList.size() <= 0) {
				return;
			}

			ProductInfo productInfo=null;
			for (ProductInfoMid productInfoMid : productInfoList) {
				try {
					//插入数据到表product_info
					productInfo=new ProductInfo();
					ConvertUtils.register(new DateConverter(null), Date.class);
					BeanUtils.copyProperties(productInfo,productInfoMid);
					productInfoService.insertProductInfo(productInfo);	
				} catch (Exception e) {
					logger.error(e.getMessage());
				}finally{
					//更新数据到表product_info_mid
					productInfoMid.setIfvalid(3);
					productInfoMidService.updateByPrimaryKey(productInfoMid);
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((ProductInfoSyncTask) ctx.getBean("productInfoSyncTask")).valid();
	}
}
