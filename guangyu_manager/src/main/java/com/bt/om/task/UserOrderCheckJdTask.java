package com.bt.om.task;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bt.om.entity.TkOrderInputJd;
import com.bt.om.entity.UserOrder;
import com.bt.om.service.ITkOrderInputJdService;
import com.bt.om.service.IUserOrderService;
import com.bt.om.util.ConfigUtil;

/**
 * 京东订单核验
 */
@Component
public class UserOrderCheckJdTask {
	private static final Logger logger = Logger.getLogger(UserOrderCheckJdTask.class);
	@Autowired
	private IUserOrderService userOrderService;
	@Autowired
	private ITkOrderInputJdService tkOrderInputJdService;

	// 每隔一段时间进行一次订单校验
	// @Scheduled(cron = "0 0 */1 * * ?")
	@Scheduled(cron = "0/30 * * * * ?")
	public void userOrderCheck() {
		logger.info("用户订单定时校验");
		UserOrder userOrder = new UserOrder();
		userOrder.setStatus1(1);
		userOrder.setStatus2(1);
		userOrder.setStatus3(1);
		List<UserOrder> userOrderList = userOrderService.selectUnCheckOrderJd(userOrder);
		if (userOrderList != null && userOrderList.size() > 0) {
			logger.info("京东共有" + userOrderList.size() + "件商品未校验");
			for (UserOrder userOrder1 : userOrderList) {
				List<TkOrderInputJd> tkOrderInputJdList = tkOrderInputJdService
						.selectByOrderId(userOrder1.getOrderId());
				double payMoney = 0;
				double commissionRate = 0;
				String productInfo = "";
				String orderStatus = "";
				int productNum = 0;
				double commission1 = 0;
				double commission2 = 0;
				double commission3 = 0;
				int status1 = 1;
				if (tkOrderInputJdList != null && tkOrderInputJdList.size() > 0) {
					for (TkOrderInputJd tkOrderInputJd : tkOrderInputJdList) {
						payMoney = payMoney + tkOrderInputJd.getActualMoney();
						commissionRate = tkOrderInputJd.getCommissionRate();
						productInfo = tkOrderInputJd.getProductName();
						orderStatus = tkOrderInputJd.getOrderStatus();
						productNum = productNum + tkOrderInputJd.getProductNum();
						commission1 = commission1 + tkOrderInputJd.getActualCommission();
						commission2 = commission2
								+ ((double) (Math.round(tkOrderInputJd.getActualCommission() * 0.8 * 100)) / 100);
						commission3 = commission3 + ((double) (Math.round(
								tkOrderInputJd.getActualCommission() * ConfigUtil.getFloat("commission.rate", 1) * 100))
								/ 100);
						if ("已结算".equals(tkOrderInputJd.getOrderStatus())||"已完成".equals(tkOrderInputJd.getOrderStatus())) {
							status1 = 2;
						} else if ("无效".equals(tkOrderInputJd.getOrderStatus())) {
							status1 = 3;
						}
					}
					if ("已付款".equals(orderStatus)) {
						orderStatus = "订单付款";
					} else if ("已结算".equals(orderStatus)||"已完成".equals(orderStatus)) {
						orderStatus = "订单结算";
					} else if ("无效".equals(orderStatus)) {
						orderStatus = "订单失效";
					}

					if (!orderStatus.equals(userOrder1.getOrderStatus())) {
						logger.info("更新京东用户订单" + userOrder1.getOrderId() + "信息");
						userOrder1.setPrice(payMoney);
						userOrder1.setRate(commissionRate);
						userOrder1.setProductNum(productNum);
						userOrder1.setProductInfo(productInfo);
						userOrder1.setOrderStatus(orderStatus);
						userOrder1.setCommission1(commission1);
						// userOrder1.setCommission2(tkOrderInput.getCommissionMoney()
						// * 0.8);
						// 佣金的基础上去掉2层支付给京东的服务费
						userOrder1.setCommission2(commission2);
						// 基本佣金的基础上计算反给客户的佣金，比例应该填小于0.8，不然亏钱
						userOrder1.setCommission3(commission3);
						userOrder1.setStatus1(status1);
						userOrder1.setUpdateTime(new Date());
						userOrderService.updateByPrimaryKey(userOrder1);
					}
				} else {
					logger.info("订单" + userOrder1.getOrderId() + "未从京东导入、或订单不存在");
				}
			}
		} else {
			logger.info("京东所有商品已校验");
		}

	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((UserOrderCheckJdTask) ctx.getBean("userOrderCheckJdTask")).userOrderCheck();
	}
}
