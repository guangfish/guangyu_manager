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

import com.bt.om.cache.JedisPool;
import com.bt.om.entity.ProductInfo;
import com.bt.om.entity.TkOrderInput;
import com.bt.om.entity.User;
import com.bt.om.entity.UserOrder;
import com.bt.om.entity.UserOrderTmp;
import com.bt.om.mapper.TkOrderInputMapper;
import com.bt.om.mapper.UserMapper;
import com.bt.om.mapper.UserOrderTmpMapper;
import com.bt.om.service.IProductInfoService;
import com.bt.om.service.IUserOrderService;
import com.bt.om.service.IUserOrderTmpService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.taobao.api.ProductApi;
import com.bt.om.taobao.api.product.ProductInfoVo;
import com.bt.om.util.DateUtil;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.NumberUtil;
import com.bt.om.util.StringUtil;

/**
 * 
 * @author Lenovo 淘宝订单自动绑定任务
 *         定时从tk_order_input表中查询数据，自动绑定订单，把未绑定的订单保存到user_order_tmp表中
 *         然后有用户订单匹配任务去生产用户订单信息 userOrderMatchTask
 *
 */
@Component
public class OrderAutoBindTask {
	private static final Logger logger = Logger.getLogger(OrderAutoBindTask.class);
	@Autowired
	private JedisPool jedisPool;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserOrderTmpMapper userOrderTmpMapper;
	@Autowired
	private TkOrderInputMapper tkOrderInputMapper;

	@Autowired
	private IUserOrderTmpService userOrderTmpService;
	@Autowired
	private IUserOrderService userOrderService;
	@Autowired
	private IProductInfoService productInfoService;

	@Scheduled(cron = "0 0/3 * * * ?")
	public void orderAutoBindTask() {
		logger.info("淘宝订单自动绑定任务");
		try {
			List<TkOrderInput> tkOrderInputList = tkOrderInputMapper.selectAll();
			if (tkOrderInputList != null && tkOrderInputList.size() > 0) {
				for (TkOrderInput tkOrderInput : tkOrderInputList) {
					// 订单失效的数据不自动绑定
					if (!"订单失效".equals(tkOrderInput.getOrderStatus())) {
						String orderId = tkOrderInput.getOrderId();
						String taobaoId = getTaobaoId(orderId);
						String adId = tkOrderInput.getAdId();
						Map<String, String> map = new HashMap<>();
						map.put("taobaoId", taobaoId);
						map.put("pid", adId);
						User user = userMapper.selectByTaobaoIdAndPid(map);
						if (user != null) {
							UserOrderTmp userOrderTmp = userOrderTmpMapper.selectByOrderId(orderId);
							if (userOrderTmp == null) {
								userOrderTmp = new UserOrderTmp();
								userOrderTmp.setMobile(user.getMobile());
								userOrderTmp.setBelong(1);
								userOrderTmp.setOrderId(orderId);
								userOrderTmp.setCreateTime(new Date());
								userOrderTmp.setUpdateTime(new Date());
								userOrderTmp.setStatus(1);
								userOrderTmpMapper.insert(userOrderTmp);
							} else {
								userOrderTmp.setStatus(1);
								userOrderTmp.setUpdateTime(new Date());
								userOrderTmpMapper.updateByPrimaryKey(userOrderTmp);
								logger.info("订单号:" + orderId + "已存在,更新状态");
							}
						} else {
							logger.info("通过订单号、广告位ID找不到用户。" + "订单号:" + orderId + " PID:" + adId);
						}
					}
				}

				userOrderCheck();

				// 清空tk_order_input表
				if (tkOrderInputList != null && tkOrderInputList.size() > 0) {
					tkOrderInputMapper.truncateTkOrderInput();
				}
			} else {
				logger.info("tk_order_input 表为空");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 用户订单绑定或重新绑定
	private void userOrderCheck() {
		logger.info("用户订单绑定或重新绑定");
		int minAgencyRewardRate = (int) (Float.parseFloat(GlobalVariable.resourceMap.get("agency_reward_rate_min"))
				* 100);
		int maxAgencyRewardRate = (int) (Float.parseFloat(GlobalVariable.resourceMap.get("agency_reward_rate_max"))
				* 100);

		List<UserOrderTmp> userOrderTmpList = userOrderTmpService.selectUnCheckOrder(1);
		if (userOrderTmpList != null && userOrderTmpList.size() > 0) {
			for (UserOrderTmp userOrderTmp : userOrderTmpList) {
				// 1:不需要重绑定 2：需要重绑定
				int ifNeedBind = 2;
				List<UserOrder> userOrderList = userOrderService.selectByOrderId(userOrderTmp.getOrderId());
				if (userOrderList != null && userOrderList.size() > 0) {
					for (UserOrder userOrder : userOrderList) {
						// 如果订单列表中有已结算的订单，那么就不删除订单
						if (userOrder.getSettleStatus() == 2) {
							ifNeedBind = 1;
							break;
						}
					}

					if (ifNeedBind == 2) {
						// 绑定订单前，先删掉已绑定未结算的订单
						logger.info("删除已帮的订单数据，订单号：" + userOrderTmp.getOrderId());
						userOrderService.deleteByOrderId(userOrderTmp.getOrderId());
					}
				}

				if (ifNeedBind == 2) {
					logger.info("订单" + userOrderTmp.getOrderId() + "为非结算状态，做订单重绑定");
					List<TkOrderInput> tkOrderInputList = tkOrderInputMapper.selectByOrderId(userOrderTmp.getOrderId());
					if (tkOrderInputList != null && tkOrderInputList.size() > 0) {
						double commission = 0;
						double commission3 = 0;
						int status1 = 1;
						String productInfoStr = "";
						ProductInfoVo productInfoVo = null;
						for (TkOrderInput tkOrderInput : tkOrderInputList) {
							UserOrder userOrder = new UserOrder();
							userOrder.setBelong(1);
							userOrder.setMobile(userOrderTmp.getMobile());
							userOrder.setProductId(tkOrderInput.getProductId());

							Object productImgObj = jedisPool.getFromCache("productImg", tkOrderInput.getProductId());
							String productImgUrl = "";
							if (productImgObj != null) {
								productImgUrl = (String) productImgObj;
								userOrder.setProductImgUrl(productImgUrl);
							} else {
								// 调用淘宝商品信息查询接口，根据商品ID获取商品图片
								productInfoStr = ProductApi.getProductInfo(tkOrderInput.getProductId());
								if (StringUtil.isNotEmpty(productInfoStr)) {
									productInfoVo = GsonUtil.GsonToBean(productInfoStr, ProductInfoVo.class);
									try {
										userOrder.setProductImgUrl(productInfoVo.getTbk_item_info_get_response()
												.getResults().getN_tbk_item().get(0).getPict_url() + "_200x200.jpg");
										jedisPool.putInCache("productImg", tkOrderInput.getProductId(),
												productInfoVo.getTbk_item_info_get_response().getResults()
														.getN_tbk_item().get(0).getPict_url() + "_200x200.jpg",
												50 * 24 * 60 * 60);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}

							userOrder.setOrderTime(DateUtil.getDateDf(tkOrderInput.getCreateTime(), DateUtil.FULL_CHINESE_PATTERN));
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
							// 佣金大于10元是，用最小的订单奖励比例
							String commissionRewardMoneyStr = GlobalVariable.resourceMap.get("commission_reward_money");
							int commissionRewardMoney = Integer.parseInt(commissionRewardMoneyStr);
							if (commission3 >= commissionRewardMoney) {
								agencyRewardRate = minAgencyRewardRate;
							} else {
								// 佣金小于10时，订单奖励范围最小20%，最大为订单奖励比例最小值+最大值
								agencyRewardRate = minAgencyRewardRate
										+ NumberUtil.getRandomNumber(0, maxAgencyRewardRate);
							}
							userOrder.setCommissionReward(
									(double) (Math.round(commission3 * (agencyRewardRate) * 100) / 100) / 100);
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

							productInfoStr = "";

						}
					} else {
						// logger.info("订单" + userOrderTmp.getOrderId() +
						// "未从阿里妈妈导入、或订单不存在");
					}
				} else {
					logger.info("订单" + userOrderTmp.getOrderId() + "为已结算状态，不做订单重绑定");
					// 更新状态
					userOrderTmp.setStatus(2);
					userOrderTmpService.update(userOrderTmp);
				}
			}
		} else {
			logger.info("淘宝所有商品已匹配");
		}
	}

	private String getTaobaoId(String orderId) {
		String taobaoId = "";
		taobaoId = orderId.substring(16, 18) + orderId.substring(14, 16);
		return taobaoId;
	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((OrderAutoBindTask) ctx.getBean("orderAutoBindTask")).orderAutoBindTask();
	}
}
