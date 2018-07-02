package com.bt.om.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.NumberUtil;

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
		String ifRun = GlobalVariable.resourceMap.get("UserOrderCheckJdTask");
		if ("1".equals(ifRun)) {
			int baseAgencyRewardRate = (int) (Float
					.parseFloat(GlobalVariable.resourceMap.get("agency_reward_rate")) * 100);
			logger.info("用户订单定时校验");
			UserOrder userOrder = new UserOrder();
			userOrder.setStatus1(1);
			userOrder.setStatus2(1);
			userOrder.setStatus3(1);
			userOrder.setBelong(2);
			List<UserOrder> userOrderList = userOrderService.selectUnCheckOrderJd(userOrder);
			if (userOrderList != null && userOrderList.size() > 0) {
				logger.info("京东共有" + userOrderList.size() + "件商品为非订单结算状态");
				for (UserOrder userOrder1 : userOrderList) {
					Map<String, Object> map = new HashMap<>();
					map.put("productId", userOrder1.getProductId());
					map.put("orderId", userOrder1.getOrderId());
					TkOrderInputJd tkOrderInputJd = tkOrderInputJdService.selectByMap(map);
					double payMoney = 0;
					double commissionRate = 0;
					String productInfo = "";
					String orderStatus = "";
					int productNum = 0;
					double commission = 0;
					int status1 = 1;
					if (tkOrderInputJd != null) {
						payMoney = tkOrderInputJd.getActualMoney();
						commissionRate = tkOrderInputJd.getCommissionRate();
						productInfo = tkOrderInputJd.getProductName();
						orderStatus = tkOrderInputJd.getOrderStatus();
						productNum = tkOrderInputJd.getProductNum();
						// 订单结算时的实际佣金
						if ("已结算".equals(tkOrderInputJd.getOrderStatus())
								|| "已完成".equals(tkOrderInputJd.getOrderStatus())) {
							commission = tkOrderInputJd.getActualCommission();
						} else {
							// 订单未结算时的预估佣金
							commission = tkOrderInputJd.getEstimateCommission();
						}

						if ("已结算".equals(tkOrderInputJd.getOrderStatus())
								|| "已完成".equals(tkOrderInputJd.getOrderStatus())) {
							status1 = 2;
						} else if ((tkOrderInputJd.getOrderStatus()).contains("无效")) {
							status1 = 3;
						}

						if ("已付款".equals(orderStatus)) {
							orderStatus = "订单付款";
						} else if ("已结算".equals(orderStatus) || "已完成".equals(orderStatus)) {
							orderStatus = "订单结算";
						} else if (orderStatus.contains("无效")) {
							orderStatus = "订单失效";
						}

						if (!orderStatus.equals(userOrder1.getOrderStatus())) {
							logger.info("更新京东用户订单" + userOrder1.getOrderId() + "信息");
							userOrder1.setPrice(((double) (Math.round(payMoney * 100)) / 100));
							userOrder1.setRate(commissionRate);
							userOrder1.setProductNum(productNum);
							userOrder1.setProductInfo(productInfo);
							userOrder1.setOrderStatus(orderStatus);
							userOrder1.setCommission1(((double) (Math.round(commission * 100)) / 100));
							// userOrder1.setCommission2(tkOrderInput.getCommissionMoney()
							// * 0.8);
							// 佣金的基础上去掉2层支付给京东的服务费
							userOrder1.setCommission2(((double) (Math.round(commission * 0.8 * 100)) / 100));
							// 基本佣金的基础上计算反给客户的佣金，比例应该填小于0.8，不然亏钱
							userOrder1.setCommission3(((double) (Math.round(commission
									* Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100))
									/ 100));
							double commission3 = ((double) (Math.round(commission
									* Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100))
									/ 100);

							if (commission3 <= 1) {
								userOrder1.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1")));
							} else if (commission3 > 1 && commission3 <= 5) {
								userOrder1.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1-5")));
							} else if (commission3 > 5 && commission3 <= 10) {
								userOrder1.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.5-10")));
							} else if (commission3 > 10 && commission3 <= 50) {
								userOrder1.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.10-50")));
							} else if (commission3 > 50 && commission3 <= 100) {
								userOrder1.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.50-100")));
							} else if (commission3 > 100 && commission3 <= 500) {
								userOrder1.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.100-500")));
							} else {
								userOrder1.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.500")));
							}

							userOrder1.setStatus1(status1);
							int agencyRewardRate = 0;
							if (commission3 >= 30) {
								agencyRewardRate = baseAgencyRewardRate;
							} else {
								agencyRewardRate = baseAgencyRewardRate + NumberUtil.getRandomNumber(0, 80);
							}
							userOrder1.setCommissionReward(
									(double) (Math.round(commission3 * (agencyRewardRate) * 100) / 100) / 100);
							userOrder1.setCommissionRewardRate(agencyRewardRate);
							userOrder1.setRewardStatus(1);
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

	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((UserOrderCheckJdTask) ctx.getBean("userOrderCheckJdTask")).userOrderCheck();
	}
}
