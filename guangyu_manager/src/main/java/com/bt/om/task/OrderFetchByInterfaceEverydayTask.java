package com.bt.om.task;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
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

import com.adtime.common.lang.StringUtil;
import com.bt.om.cache.JedisPool;
import com.bt.om.entity.ProductInfo;
import com.bt.om.entity.TkOrderInput;
import com.bt.om.entity.User;
import com.bt.om.entity.UserOrder;
import com.bt.om.entity.UserOrderTmp;
import com.bt.om.mapper.TkOrderInputMapper;
import com.bt.om.mapper.UserMapper;
import com.bt.om.mapper.UserOrderTmpMapper;
import com.bt.om.report.vo.taobao.DateVo;
import com.bt.om.report.vo.taobao.N_tbk_order;
import com.bt.om.report.vo.taobao.RootErr;
import com.bt.om.report.vo.taobao.RootMore;
import com.bt.om.report.vo.taobao.RootOne;
import com.bt.om.service.IProductInfoService;
import com.bt.om.service.ITkOrderInputService;
import com.bt.om.service.IUserOrderService;
import com.bt.om.service.IUserOrderTmpService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.taobao.api.ProductApi;
import com.bt.om.taobao.api.product.ProductInfoVo;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.DateUtil;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.HttpcomponentsUtil;
import com.bt.om.util.NumberUtil;

/**
 * 
 * @author Lenovo 通过三方API接口定时拉去报表数据 每天增量获取报表数据 6点全量获取报表数据
 */
@Component
public class OrderFetchByInterfaceEverydayTask {
	private static final Logger logger = Logger.getLogger(OrderFetchByInterfaceEverydayTask.class);
	@Autowired
	private ITkOrderInputService tkOrderInputService;
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

	@Scheduled(cron = "0 0/15 * * * ?")
	public void getTask() {
		logger.info("通过三方API接口定时拉取增量报表数据");
		String remoteTaskUrl = "https://api.open.21ds.cn/apiv1/gettkorder";
		String uri = "";
		String startTime = "";
		String encodedStartTime = "";
		int timeInterval = 1200;// 秒
		// 当前时间之前20分钟
		startTime = DateUtil.dateFormate(DateUtil.addSeconds(new Date(), -timeInterval), DateUtil.FULL_CHINESE_PATTERN);
		try {
			encodedStartTime = URLEncoder.encode(startTime, "UTF-8");
		} catch (Exception e) {
			logger.error("时间转码失败");
		}

		int page = 1;
		while (true) {
			StringBuffer sb = new StringBuffer();
			logger.info("获取时间【" + startTime + "】开始，第【" + page + "】页数据");
			sb.append("?").append("apkey=").append("bdbaee0e-8ADD-a970-1937-d507af6a1118").append("&starttime=")
					.append(encodedStartTime).append("&span=").append(timeInterval + "").append("&page=").append(page)
					.append("&pagesize=").append("100").append("&tkstatus=").append("1").append("&ordertype=")
					.append("create_time").append("&tbname=").append("chj8023");

			uri = sb.toString();
			String retJson = "";
			try {
				retJson = HttpcomponentsUtil.getHttpsJson(remoteTaskUrl + uri);
				logger.info(retJson);
			} catch (Exception e) {
				logger.info("报表抓取接口调用失败");
				logger.info(e.getMessage());
				try {
					logger.info("睡眠10秒后再次抓取报表数据");
					Thread.sleep(10000);
					retJson = HttpcomponentsUtil.getHttpsJson(remoteTaskUrl + uri);
				} catch (Exception e1) {
					logger.info("再次尝试抓取报表数据失败");
					logger.info(e.getMessage());
					break;
				}
			}

			DateVo dateVo = new DateVo();
			try {
				RootMore rootMore = GsonUtil.GsonToBean(retJson, RootMore.class);
				dateVo.setN_tbk_order_list(rootMore.getData().getN_tbk_order());
			} catch (Exception e) {
				logger.info("返回data为非数组列表");
				try {
					RootOne rootOne = GsonUtil.GsonToBean(retJson, RootOne.class);
					List<N_tbk_order> n_tbk_order_list = new ArrayList<>();
					n_tbk_order_list.add(rootOne.getData().getN_tbk_order());
					dateVo.setN_tbk_order_list(n_tbk_order_list);
				} catch (Exception e1) {
					logger.info("返回data为非对象");
					try {
						RootErr rootErr = GsonUtil.GsonToBean(retJson, RootErr.class);
						logger.info("返回数据错误码：" + rootErr.getCode());
						logger.info("查不到订单数据，直接退出循环");
						break;
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
			if (dateVo.getN_tbk_order_list() != null && dateVo.getN_tbk_order_list().size() > 0) {
				insertToTkOrderInput(dateVo.getN_tbk_order_list());
			}
			page = page + 1;
		}

		// 订单自动绑定或重新绑定
		orderAutoBindTask();

		// 每天6点执行全量获取报表任务

		int hour = Integer.parseInt(DateUtil.dateFormate(new Date(), "HH"));
		if (hour == 6) {
			Object orderFetchAll = jedisPool.getFromCache("orderFetchAll",
					DateUtil.dateFormate(new Date(), DateUtil.CHINESE_PATTERN));
			if (orderFetchAll == null) {
				getAllReport();
				// 订单自动绑定或重新绑定
				orderAutoBindTask();
				jedisPool.putInCache("orderFetchAll", DateUtil.dateFormate(new Date(), DateUtil.CHINESE_PATTERN),
						DateUtil.dateFormate(new Date(), DateUtil.CHINESE_PATTERN), 2 * 24 * 60 * 60);
			}
		}
	}

	private void insertToTkOrderInput(List<N_tbk_order> n_tbk_order_list) {
		if (n_tbk_order_list != null && n_tbk_order_list.size() > 0) {
			for (N_tbk_order n_tbk_order : n_tbk_order_list) {
				TkOrderInput tkOrderInput = new TkOrderInput();
				tkOrderInput.setAccount(ConfigUtil.getString("alimama.account"));
				if ("169978395".equals(n_tbk_order.getAdzone_id())) {
					tkOrderInput.setAdId("176864894");
				} else {
					tkOrderInput.setAdId(n_tbk_order.getAdzone_id());
				}
				tkOrderInput.setAdName(n_tbk_order.getAdzone_name());
				tkOrderInput.setCatName(n_tbk_order.getAuction_category());
				tkOrderInput.setClickTime(n_tbk_order.getClick_time());
				if (StringUtil.isNotEmpty(n_tbk_order.getTotal_commission_fee())) {
					tkOrderInput.setCommissionMoney(Double.parseDouble(n_tbk_order.getTotal_commission_fee()));
				} else {
					tkOrderInput.setCommissionMoney(0d);
				}
				tkOrderInput.setCommissionRate(Double.parseDouble(n_tbk_order.getTotal_commission_rate()) * 100);
				tkOrderInput.setCreateTime(n_tbk_order.getCreate_time());
				tkOrderInput.setDealPlatform(n_tbk_order.getTerminal_type());
				// 分成比例
				tkOrderInput.setDivideRate(Double.parseDouble(n_tbk_order.getCommission_rate()) * 100);
				// 效果预估
				tkOrderInput.setEffectEstimate(Double
						.parseDouble(NumberUtil.formatDouble(Double.parseDouble(n_tbk_order.getAlipay_total_price())
								* Double.parseDouble(n_tbk_order.getTotal_commission_rate()), "0.00")));
				// 预估收入
				tkOrderInput.setEstimateIncome(Double.parseDouble(n_tbk_order.getCommission()));
				// 收入比例（卖家设置佣金比率+平台补贴比率）
				tkOrderInput.setIncomeRate(Double.parseDouble(n_tbk_order.getIncome_rate()));
				tkOrderInput.setOrderId(n_tbk_order.getTrade_parent_id());
				String orderStatus = n_tbk_order.getTk_status();
				if ("3".equals(orderStatus) || "14".equals(orderStatus)) {
					orderStatus = "订单结算";
				} else if ("12".equals(orderStatus)) {
					orderStatus = "订单付款";
				} else if ("13".equals(orderStatus)) {
					orderStatus = "订单失效";
				}
				tkOrderInput.setOrderStatus(orderStatus);
				tkOrderInput.setOrderType(n_tbk_order.getOrder_type());
				// 付款金额
				tkOrderInput.setPayMoney(Double.parseDouble(n_tbk_order.getAlipay_total_price()));
				// 商品单价
				tkOrderInput.setPrice(Double.parseDouble(n_tbk_order.getPrice()));
				tkOrderInput.setProductId(n_tbk_order.getNum_iid());
				tkOrderInput.setProductInfo(n_tbk_order.getItem_title());
				tkOrderInput.setProductNum(Integer.parseInt(n_tbk_order.getItem_num()));
				tkOrderInput.setSellerWangwang(n_tbk_order.getSeller_nick());
				// 结算金额
				tkOrderInput.setSettleMoney(Double.parseDouble(n_tbk_order.getPay_price()));
				if (StringUtil.isNotEmpty(n_tbk_order.getEarning_time())) {
					tkOrderInput.setSettleTime(n_tbk_order.getEarning_time());
				} else {
					tkOrderInput.setSettleTime("");
				}
				tkOrderInput.setShopName(n_tbk_order.getSeller_shop_title());
				tkOrderInput.setSourceMediaId(n_tbk_order.getSite_id());
				tkOrderInput.setSourceMediaName(n_tbk_order.getSite_name());
				if (StringUtil.isNotEmpty(n_tbk_order.getSubsidy_fee())) {
					tkOrderInput.setSubsidyMoney(Double.parseDouble(n_tbk_order.getSubsidy_fee()));
				} else {
					tkOrderInput.setSubsidyMoney(0d);
				}
				tkOrderInput.setSubsidyRate(Double.parseDouble(n_tbk_order.getSubsidy_rate()) * 100);
				tkOrderInput.setSubsidyType(n_tbk_order.getSubsidy_type());
				tkOrderInput.setTechService(10d);
				tkOrderInput.setThirdServiceFrom(n_tbk_order.getTk3rd_type());
				tkOrderInput.setUpdateTime(new Date());

				tkOrderInputService.insert(tkOrderInput);
			}
		}

	}

	// 淘宝订单自动绑定
	private void orderAutoBindTask() {
		logger.info("淘宝订单自动绑定");
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

							userOrder.setOrderTime(
									DateUtil.getDateDf(tkOrderInput.getCreateTime(), DateUtil.FULL_CHINESE_PATTERN));
							userOrder.setOrderId(userOrderTmp.getOrderId());
							userOrder.setPrice(((double) (Math.round(tkOrderInput.getPayMoney() * 100)) / 100));
							userOrder.setRate(((double) (Math.round(tkOrderInput.getCommissionRate() * 100)) / 100));
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

	// 全量拉取订单报表信息
	private void getAllReport() {
		logger.info("通过三方API接口定时拉取全量报表数据");
		String remoteTaskUrl = "https://api.open.21ds.cn/apiv1/gettkorder";
		int timeInterval = 1200;// 秒
		// 当前日
		int thisDay = Integer.parseInt(DateUtil.dateFormate(new Date(), "dd"));
		String startDatetime = "";
		String endDatetime = "";
		if (thisDay >= 1 && thisDay <= 20) {
			logger.info("当前时间在1-20之间，拉取上月+本月截至今日前一天数据");
			// 上月1日
			startDatetime = DateUtil.dateFormate(DateUtil.getBeforeMonth(new Date()), DateUtil.MONTH_PATTERN)
					+ "-01 00:00:00";
			// 今日00:00:00
			endDatetime = DateUtil.dateFormate(new Date(), DateUtil.CHINESE_PATTERN) + " 00:00:00";
		} else {
			logger.info("当前时间在21日以后到月底，拉取本月截至今日前一天数据");
			// 本月1日
			startDatetime = DateUtil.dateFormate(new Date(), DateUtil.MONTH_PATTERN) + "-01 00:00:00";
			// 今日00:00:00
			endDatetime = DateUtil.dateFormate(new Date(), DateUtil.CHINESE_PATTERN) + " 00:00:00";
		}

		String uri = "";

		// 判断统计开始时间是否小于结束时间
		while (DateUtil.compareDateTime(startDatetime, endDatetime)) {
			String encodedStartDatetime = "";
			try {
				encodedStartDatetime = URLEncoder.encode(startDatetime, "UTF-8");
			} catch (UnsupportedEncodingException e3) {
				e3.printStackTrace();
			}
			logger.info(startDatetime);
			int page = 1;
			while (true) {
				StringBuffer sb = new StringBuffer();
				logger.info("获取时间【" + startDatetime + "】开始，第【" + page + "】页数据");
				sb.append("?").append("apkey=").append("bdbaee0e-8ADD-a970-1937-d507af6a1118").append("&starttime=")
						.append(encodedStartDatetime).append("&span=").append(timeInterval + "").append("&page=")
						.append(page).append("&pagesize=").append("100").append("&tkstatus=").append("1")
						.append("&ordertype=").append("create_time").append("&tbname=").append("chj8023");

				uri = sb.toString();
				String retJson = "";
				try {
					retJson = HttpcomponentsUtil.getHttpsJson(remoteTaskUrl + uri);
					logger.info(retJson);
				} catch (Exception e) {
					logger.info("报表抓取接口调用失败");
					logger.info(e.getMessage());
					try {
						logger.info("睡眠10秒后再次抓取报表数据");
						Thread.sleep(10000);
						retJson = HttpcomponentsUtil.getHttpsJson(remoteTaskUrl + uri);
					} catch (Exception e1) {
						logger.info("再次尝试抓取报表数据失败");
						logger.info(e.getMessage());
						break;
					}
				}

				DateVo dateVo = new DateVo();
				try {
					RootMore rootMore = GsonUtil.GsonToBean(retJson, RootMore.class);
					dateVo.setN_tbk_order_list(rootMore.getData().getN_tbk_order());
				} catch (Exception e) {
					logger.info("返回data为非数组列表");
					try {
						RootOne rootOne = GsonUtil.GsonToBean(retJson, RootOne.class);
						List<N_tbk_order> n_tbk_order_list = new ArrayList<>();
						n_tbk_order_list.add(rootOne.getData().getN_tbk_order());
						dateVo.setN_tbk_order_list(n_tbk_order_list);
					} catch (Exception e1) {
						logger.info("返回data为非对象");
						try {
							RootErr rootErr = GsonUtil.GsonToBean(retJson, RootErr.class);
							logger.info("返回数据错误码：" + rootErr.getCode());
							logger.info("查不到订单数据，退出循环");
							break;
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}
				if (dateVo.getN_tbk_order_list() != null && dateVo.getN_tbk_order_list().size() > 0) {
					insertToTkOrderInput(dateVo.getN_tbk_order_list());
				}
				page = page + 1;
			}

			// 增加1200秒时间
			startDatetime = DateUtil.dateFormate(
					DateUtil.addSeconds(DateUtil.getDateTime(startDatetime, new Date()), timeInterval),
					DateUtil.FULL_CHINESE_PATTERN);
		}
	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		@SuppressWarnings("resource")
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((OrderFetchByInterfaceEverydayTask) ctx.getBean("orderFetchByInterfaceEverydayTask")).getTask();
	}
}
