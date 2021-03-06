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
import com.bt.om.taobao.api.ProductApi;
import com.bt.om.taobao.api.product.ProductInfoVo;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.NumberUtil;
import com.bt.om.util.StringUtil;

/**
 * 用户订单匹配
 */
//@Component
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
					//1:不需要绑定 2：需要绑定
					int ifNeedBind=1;
					List<UserOrder> userOrderList=userOrderService.selectByOrderId(userOrderTmp.getOrderId());
					if(userOrderList!=null && userOrderList.size()>0){
						if(userOrderList.get(0).getSettleStatus()==1){
							//绑定订单前，先删掉已绑定未结算的订单
							userOrderService.deleteByOrderId(userOrderTmp.getOrderId());
							ifNeedBind=2;
						}
					}
					
					if(ifNeedBind==2){
						List<TkOrderInput> tkOrderInputList = tkOrderInputService
								.selectByOrderId(userOrderTmp.getOrderId());
						if (tkOrderInputList != null && tkOrderInputList.size() > 0) {
							double commission = 0;
							double commission3 = 0;
							int status1 = 1;
							String productInfoStr="";
							ProductInfoVo productInfoVo=null;
							for (TkOrderInput tkOrderInput : tkOrderInputList) {
								UserOrder userOrder = new UserOrder();
								userOrder.setBelong(1);
								userOrder.setMobile(userOrderTmp.getMobile());
								userOrder.setProductId(tkOrderInput.getProductId());
								
								//调用淘宝商品信息查询接口，根据商品ID获取商品图片
								productInfoStr = ProductApi.getProductInfo(tkOrderInput.getProductId());
								if (StringUtil.isNotEmpty(productInfoStr)) {
									productInfoVo = GsonUtil.GsonToBean(productInfoStr, ProductInfoVo.class);
									try {
										userOrder.setProductImgUrl(productInfoVo.getTbk_item_info_get_response()
												.getResults().getN_tbk_item().get(0).getPict_url() + "_200x200.jpg");
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								
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
								
								userOrder.setFanliMultiple(1f);

								if ("订单结算".equals(tkOrderInput.getOrderStatus())) {
									status1 = 2;
								} else if ("订单失效".equals(tkOrderInput.getOrderStatus())) {
									status1 = 3;
								}
								userOrder.setStatus1(status1);
								userOrder.setStatus2(1);
								userOrder.setStatus3(1);
								userOrder.setSettleStatus(1);
								int agencyRewardRate = 0;
								//佣金大于10元是，用最小的订单奖励比例
								String commissionRewardMoneyStr=GlobalVariable.resourceMap.get("commission_reward_money");
								int commissionRewardMoney=Integer.parseInt(commissionRewardMoneyStr);
								if (commission3 >= commissionRewardMoney) {
									agencyRewardRate = minAgencyRewardRate;
								} else {
									//佣金小于10时，订单奖励范围最小20%，最大为订单奖励比例最小值+最大值
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
								
								productInfoStr="";
								
							}
						} else {
//							logger.info("订单" + userOrderTmp.getOrderId() + "未从阿里妈妈导入、或订单不存在");
						}
					}else{
						// 更新状态
						userOrderTmp.setStatus(2);
						userOrderTmpService.update(userOrderTmp);
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
							if ("已付款".equals(orderStatus)) {
								orderStatus = "订单付款";
							} else if ("已结算".equals(orderStatus) || "已完成".equals(orderStatus)) {
								orderStatus = "订单结算";
							} else if (orderStatus.contains("无效")) {
								orderStatus = "订单失效";
							}
							
//							if ("已付款".equals(orderStatus) || "已完成".equals(orderStatus)) {
//								orderStatus = "订单付款";
//							} else if ("已结算".equals(orderStatus) ) {
//								orderStatus = "订单结算";
//							} else if (orderStatus.contains("无效")) {
//								orderStatus = "订单失效";
//							}
							userOrder.setOrderStatus(orderStatus);

							// 订单结算时的实际佣金
							if ("已结算".equals(tkOrderInputJd.getOrderStatus())
									|| "已完成".equals(tkOrderInputJd.getOrderStatus())) {
								commission = tkOrderInputJd.getActualCommission();
							} else {
								// 订单未结算时的预估佣金
								commission = tkOrderInputJd.getEstimateCommission();
							}
							
//							// 订单结算时的实际佣金
//							if ("已结算".equals(tkOrderInputJd.getOrderStatus())) {
//								commission = tkOrderInputJd.getActualCommission();
//							} else {
//								// 订单未结算时的预估佣金
//								commission = tkOrderInputJd.getEstimateCommission();
//							}
							
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
							
							userOrder.setFanliMultiple(1f);

							if ("已结算".equals(tkOrderInputJd.getOrderStatus())
									|| "已完成".equals(tkOrderInputJd.getOrderStatus())) {
								status1 = 2;
							} else if ((tkOrderInputJd.getOrderStatus()).contains("无效")) {
								status1 = 3;
							}
							
							userOrder.setStatus1(status1);
							userOrder.setStatus2(1);
							userOrder.setStatus3(1);
							userOrder.setSettleStatus(1);
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
//						logger.info("订单" + userOrderTmp.getOrderId() + "未从京东联盟导入、或订单不存在");
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
