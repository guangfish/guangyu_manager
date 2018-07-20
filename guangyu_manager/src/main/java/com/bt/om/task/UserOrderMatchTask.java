package com.bt.om.task;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bt.om.entity.ProductInfo;
import com.bt.om.entity.TkOrderInput;
import com.bt.om.entity.TkOrderInputJd;
import com.bt.om.entity.UserOrder;
import com.bt.om.entity.UserOrderTmp;
import com.bt.om.service.IProductInfoService;
import com.bt.om.service.ITkOrderInputJdService;
import com.bt.om.service.ITkOrderInputService;
import com.bt.om.service.IUserOrderService;
import com.bt.om.service.IUserOrderTmpService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.NumberUtil;

/**
 * 用户订单匹配
 */
@Component
public class UserOrderMatchTask {
	private static final Logger logger = Logger.getLogger(UserOrderMatchTask.class);
	@Autowired
	private IUserOrderTmpService userOrderTmpService;
	@Autowired
	private IUserOrderService userOrderService;
	@Autowired
	private ITkOrderInputService tkOrderInputService;
	@Autowired
	private ITkOrderInputJdService tkOrderInputJdService;
	@Autowired
	private IProductInfoService productInfoService;

	// 每隔一段时间进行一次订单校验
	// @Scheduled(cron = "0 0 */1 * * ?")
	@Scheduled(cron = "0/30 * * * * ?")
	public void userOrderCheck() {
		String ifRun = GlobalVariable.resourceMap.get("UserOrderMatchTask");
		if ("1".equals(ifRun)) {
			int minAgencyRewardRate = (int) (Float
					.parseFloat(GlobalVariable.resourceMap.get("agency_reward_rate_min")) * 100);
			int maxAgencyRewardRate = (int) (Float
					.parseFloat(GlobalVariable.resourceMap.get("agency_reward_rate_max")) * 100);
			logger.info("用户订单定时匹配");
			// for 淘宝
			List<UserOrderTmp> userOrderTmpList = userOrderTmpService.selectUnCheckOrder(1);
			if (userOrderTmpList != null && userOrderTmpList.size() > 0) {
				for (UserOrderTmp userOrderTmp : userOrderTmpList) {
					List<TkOrderInput> tkOrderInputList = tkOrderInputService
							.selectByOrderId(userOrderTmp.getOrderId());
					if (tkOrderInputList != null && tkOrderInputList.size() > 0) {
						double commission = 0;
						double commission3 = 0;
						int status1 = 1;
						for (TkOrderInput tkOrderInput : tkOrderInputList) {
							UserOrder userOrder = new UserOrder();
							userOrder.setBelong(1);
							userOrder.setMobile(userOrderTmp.getMobile());
							userOrder.setProductId(tkOrderInput.getProductId());
							userOrder.setOrderId(userOrderTmp.getOrderId());
							userOrder.setPrice(((double) (Math.round(tkOrderInput.getPayMoney() * 100)) / 100));
							userOrder.setRate(tkOrderInput.getCommissionRate());
							userOrder.setShopName(tkOrderInput.getShopName());
							userOrder.setProductNum(tkOrderInput.getProductNum());
							userOrder.setProductInfo(tkOrderInput.getProductInfo());
							userOrder.setOrderStatus(tkOrderInput.getOrderStatus());
							// 订单结算时的实际佣金
							if ("订单结算".equals(tkOrderInput.getOrderStatus())) {
								commission = tkOrderInput.getCommissionMoney();
							} else {
								// 订单未结算时的预估佣金
								commission = tkOrderInput.getEffectEstimate();
							}
							userOrder.setCommission1(((double) (Math.round(commission * 100)) / 100));
							// 佣金的基础上去掉2层支付给阿里妈妈的服务费
							userOrder.setCommission2(((double) (Math.round(commission * 0.8 * 100)) / 100));
							// 基本佣金的基础上计算反给客户的佣金，比例应该填小于0.8，不然亏钱
							userOrder.setCommission3(((double) (Math.round(commission
									* Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100))
									/ 100));
							commission3 = ((double) (Math.round(commission
									* Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100))
									/ 100);
							if (commission3 <= 1) {
								userOrder.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1")));
							} else if (commission3 > 1 && commission3 <= 5) {
								userOrder.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1-5")));
							} else if (commission3 > 5 && commission3 <= 10) {
								userOrder.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.5-10")));
							} else if (commission3 > 10 && commission3 <= 50) {
								userOrder.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.10-50")));
							} else if (commission3 > 50 && commission3 <= 100) {
								userOrder.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.50-100")));
							} else if (commission3 > 100 && commission3 <= 500) {
								userOrder.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.100-500")));
							} else {
								userOrder.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.500")));
							}

							if ("订单结算".equals(tkOrderInput.getOrderStatus())) {
								status1 = 2;
							} else if ("订单失效".equals(tkOrderInput.getOrderStatus())) {
								status1 = 3;
							}
							userOrder.setStatus1(status1);
							userOrder.setStatus2(1);
							userOrder.setStatus3(1);
							int agencyRewardRate = 0;
							if (commission3 >= 30) {
								agencyRewardRate = minAgencyRewardRate;
							} else {
								agencyRewardRate = minAgencyRewardRate + NumberUtil.getRandomNumber(0, maxAgencyRewardRate);
							}
							userOrder.setCommissionReward(
									(double) (Math.round(commission3 * (agencyRewardRate) * 100)/100) / 100);
							userOrder.setCommissionRewardRate(agencyRewardRate);
							userOrder.setRewardStatus(1);
							userOrder.setCreateTime(new Date());
							userOrder.setUpdateTime(new Date());

							ProductInfo productInfo = productInfoService.getByProductId(tkOrderInput.getProductId());
							if (productInfo != null) {
								userOrder.setProductImgUrl(productInfo.getProductImgUrl());
							}
							userOrderService.insert(userOrder);

							// 更新状态
							userOrderTmp.setStatus(2);
							userOrderTmpService.update(userOrderTmp);
						}
					} else {
						logger.info("订单" + userOrderTmp.getOrderId() + "未从阿里妈妈导入、或订单不存在");
					}
				}
			} else {
				logger.info("淘宝所有商品已匹配");
			}

			// for 京东
			userOrderTmpList = userOrderTmpService.selectUnCheckOrder(2);
			if (userOrderTmpList != null && userOrderTmpList.size() > 0) {
				for (UserOrderTmp userOrderTmp : userOrderTmpList) {
					List<TkOrderInputJd> tkOrderInputJdList = tkOrderInputJdService
							.selectByOrderId(userOrderTmp.getOrderId());
					if (tkOrderInputJdList != null && tkOrderInputJdList.size() > 0) {
						double commission = 0;
						double commission3 = 0;
						int status1 = 1;
						for (TkOrderInputJd tkOrderInputJd : tkOrderInputJdList) {
							UserOrder userOrder = new UserOrder();
							userOrder.setBelong(2);
							userOrder.setMobile(userOrderTmp.getMobile());
							userOrder.setProductId(tkOrderInputJd.getProductId());
							userOrder.setOrderId(userOrderTmp.getOrderId());
							userOrder.setPrice(((double) (Math.round(tkOrderInputJd.getActualMoney() * 100)) / 100));
							userOrder.setRate(tkOrderInputJd.getCommissionRate());
							userOrder.setShopName("");
							userOrder.setProductNum(tkOrderInputJd.getProductNum());
							userOrder.setProductInfo(tkOrderInputJd.getProductName());
							String orderStatus = tkOrderInputJd.getOrderStatus();
//							if ("已付款".equals(orderStatus)) {
//								orderStatus = "订单付款";
//							} else if ("已结算".equals(orderStatus) || "已完成".equals(orderStatus)) {
//								orderStatus = "订单结算";
//							} else if ("无效".equals(orderStatus)) {
//								orderStatus = "订单失效";
//							}
							
							if ("已付款".equals(orderStatus) || "已完成".equals(orderStatus)) {
								orderStatus = "订单付款";
							} else if ("已结算".equals(orderStatus) ) {
								orderStatus = "订单结算";
							} else if (orderStatus.contains("无效")) {
								orderStatus = "订单失效";
							}
							userOrder.setOrderStatus(orderStatus);

//							// 订单结算时的实际佣金
//							if ("已结算".equals(tkOrderInputJd.getOrderStatus())
//									|| "已完成".equals(tkOrderInputJd.getOrderStatus())) {
//								commission = tkOrderInputJd.getActualCommission();
//							} else {
//								// 订单未结算时的预估佣金
//								commission = tkOrderInputJd.getEstimateCommission();
//							}
							
							// 订单结算时的实际佣金
							if ("已结算".equals(tkOrderInputJd.getOrderStatus())) {
								commission = tkOrderInputJd.getActualCommission();
							} else {
								// 订单未结算时的预估佣金
								commission = tkOrderInputJd.getEstimateCommission();
							}
							
							userOrder.setCommission1(((double) (Math.round(commission * 100)) / 100));
							// 佣金的基础上去掉2层支付给阿里妈妈的服务费
							userOrder.setCommission2(((double) (Math.round(commission * 0.8 * 100)) / 100));
							// 基本佣金的基础上计算反给客户的佣金，比例应该填小于0.8，不然亏钱
							userOrder.setCommission3(((double) (Math.round(commission
									* Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100))
									/ 100));
							commission3 = ((double) (Math.round(commission
									* Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100))
									/ 100);
							if (commission3 <= 1) {
								userOrder.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1")));
							} else if (commission3 > 1 && commission3 <= 5) {
								userOrder.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1-5")));
							} else if (commission3 > 5 && commission3 <= 10) {
								userOrder.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.5-10")));
							} else if (commission3 > 10 && commission3 <= 50) {
								userOrder.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.10-50")));
							} else if (commission3 > 50 && commission3 <= 100) {
								userOrder.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.50-100")));
							} else if (commission3 > 100 && commission3 <= 500) {
								userOrder.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.100-500")));
							} else {
								userOrder.setFanliMultiple(
										Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.500")));
							}

//							if ("已结算".equals(tkOrderInputJd.getOrderStatus())
//									|| "已完成".equals(tkOrderInputJd.getOrderStatus())) {
//								status1 = 2;
//							} else if ((tkOrderInputJd.getOrderStatus()).contains("无效")) {
//								status1 = 3;
//							}
							
							if ("已结算".equals(tkOrderInputJd.getOrderStatus())) {
								status1 = 2;
							} else if ((tkOrderInputJd.getOrderStatus()).contains("无效")) {
								status1 = 3;
							}
							userOrder.setStatus1(status1);
							userOrder.setStatus2(1);
							userOrder.setStatus3(1);
							int agencyRewardRate = 0;
							if (commission3 >= 30) {
								agencyRewardRate = minAgencyRewardRate;
							} else {
								agencyRewardRate = minAgencyRewardRate + NumberUtil.getRandomNumber(0, maxAgencyRewardRate);
							}
							userOrder.setCommissionReward(
									(double) (Math.round(commission3 * (agencyRewardRate) * 100)/100) / 100);
							userOrder.setCommissionRewardRate(agencyRewardRate);
							userOrder.setRewardStatus(1);
							userOrder.setCreateTime(new Date());
							userOrder.setUpdateTime(new Date());

							ProductInfo productInfo = productInfoService.getByProductId(tkOrderInputJd.getProductId());
							if (productInfo != null) {
								userOrder.setProductImgUrl(productInfo.getProductImgUrl());
							}
							userOrderService.insert(userOrder);

							// 更新状态
							userOrderTmp.setStatus(2);
							userOrderTmpService.update(userOrderTmp);
						}
					} else {
						logger.info("订单" + userOrderTmp.getOrderId() + "未从京东联盟导入、或订单不存在");
					}
				}
			} else {
				logger.info("京东所有商品已匹配");
			}
		}

	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((UserOrderMatchTask) ctx.getBean("userOrderCheckTask")).userOrderCheck();
	}
}
