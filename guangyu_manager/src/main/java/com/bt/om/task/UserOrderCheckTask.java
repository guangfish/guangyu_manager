package com.bt.om.task;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bt.om.entity.TkOrderInput;
import com.bt.om.entity.UserOrder;
import com.bt.om.service.ITkOrderInputService;
import com.bt.om.service.IUserOrderService;
import com.bt.om.util.ConfigUtil;

/**
 * 淘宝订单核验
 */
@Component
public class UserOrderCheckTask {
	private static final Logger logger = Logger.getLogger(UserOrderCheckTask.class);
	@Autowired
	private IUserOrderService userOrderService;
	@Autowired
	private ITkOrderInputService tkOrderInputService;

	// 每隔一段时间进行一次订单校验
	// @Scheduled(cron = "0 0 */1 * * ?")
	@Scheduled(cron = "0/30 * * * * ?")
	public void userOrderCheck() {
		logger.info("用户订单定时校验");
		UserOrder userOrder = new UserOrder();
		userOrder.setStatus1(1);
		userOrder.setStatus2(1);
		userOrder.setStatus3(1);
		List<UserOrder> userOrderList = userOrderService.selectUnCheckOrderTaobao(userOrder);
		if (userOrderList != null && userOrderList.size() > 0) {
			logger.info("淘宝共有" + userOrderList.size() + "件商品未校验");
			for (UserOrder userOrder1 : userOrderList) {
				List<TkOrderInput> tkOrderInputList = tkOrderInputService.selectByOrderId(userOrder1.getOrderId());
				double payMoney = 0;
				double commissionRate = 0;
				String shopName = "";
				String productInfo = "";
				String orderStatus = "";
				int productNum = 0;
				double commission1 = 0;
				double commission2 = 0;
				double commission3 = 0;
				int status1 = 1;
				if (tkOrderInputList != null && tkOrderInputList.size() > 0) {
					for (TkOrderInput tkOrderInput : tkOrderInputList) {
						payMoney = payMoney + tkOrderInput.getPayMoney();
						commissionRate = tkOrderInput.getCommissionRate();
						shopName = tkOrderInput.getShopName();
						productInfo = tkOrderInput.getProductInfo();
						orderStatus = tkOrderInput.getOrderStatus();
						productNum = productNum + tkOrderInput.getProductNum();
						commission1 = commission1 + tkOrderInput.getCommissionMoney();
						commission2 = commission2 + tkOrderInput.getCommissionMoney();
						commission3 = commission3 + tkOrderInput.getCommissionMoney();
						if ("订单结算".equals(tkOrderInput.getOrderStatus())) {
							status1 = 2;
						} else if ("订单失效".equals(tkOrderInput.getOrderStatus())) {
							status1 = 3;
						}
					}
					if (!orderStatus.equals(userOrder1.getOrderStatus())) {
						logger.info("更新淘宝用户订单" + userOrder1.getOrderId() + "信息");
						userOrder1.setPrice(payMoney);
						userOrder1.setRate(commissionRate);
						userOrder1.setShopName(shopName);
						userOrder1.setProductNum(productNum);
						userOrder1.setProductInfo(productInfo);
						userOrder1.setOrderStatus(orderStatus);
						userOrder1.setCommission1(((double) (Math.round(commission1 * 100)) / 100));
						// userOrder1.setCommission2(tkOrderInput.getCommissionMoney()
						// * 0.8);
						// 佣金的基础上去掉2层支付给阿里妈妈的服务费
						userOrder1.setCommission2(((double) (Math.round(commission2 * 0.8 * 100)) / 100));
						// 基本佣金的基础上计算反给客户的佣金，比例应该填小于0.8，不然亏钱
						userOrder1.setCommission3(
								((double) (Math.round(commission3 * ConfigUtil.getFloat("commission.rate", 1) * 100))
										/ 100));
						userOrder1.setStatus1(status1);
						userOrder1.setUpdateTime(new Date());
						userOrderService.updateByPrimaryKey(userOrder1);
					}
				} else {
					logger.info("订单" + userOrder1.getOrderId() + "未从阿里妈妈导入、或订单不存在");
				}
			}
		} else {
			logger.info("淘宝所有商品已校验");
		}

	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((UserOrderCheckTask) ctx.getBean("userOrderCheckTask")).userOrderCheck();
	}
}
