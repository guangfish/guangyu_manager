package com.bt.om.task;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.adtime.common.lang.StringUtil;
import com.bt.om.entity.TkOrderInput;
import com.bt.om.report.vo.taobao.DateVo;
import com.bt.om.report.vo.taobao.N_tbk_order;
import com.bt.om.report.vo.taobao.RootErr;
import com.bt.om.report.vo.taobao.RootMore;
import com.bt.om.report.vo.taobao.RootOne;
import com.bt.om.service.ITkOrderInputService;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.DateUtil;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.HttpcomponentsUtil;
import com.bt.om.util.NumberUtil;

/**
 * 
 * @author Lenovo 通过三方API接口定时拉去报表数据 每天全量获取报表数据 1-20之间包括20，获取上月+本月截至统计时间前一天的所有数据
 *         大于21日，获取本月截至统计时间前一天的所有数据
 */
//@Component
public class OrderFetchByInterfaceMoredayTask {
	private static final Logger logger = Logger.getLogger(OrderFetchByInterfaceMoredayTask.class);
	@Autowired
	private ITkOrderInputService tkOrderInputService;

	@Scheduled(cron = "0 0 6 * * ?")
	public void getTask() {
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
			String encodedStartDatetime="";
			try {
				encodedStartDatetime=URLEncoder.encode(startDatetime, "UTF-8");
			} catch (UnsupportedEncodingException e3) {
				e3.printStackTrace();
			}
			logger.info(startDatetime);
			int page = 1;
			while (true) {
				StringBuffer sb = new StringBuffer();
				logger.info("获取时间【" + startDatetime + "】开始，第【" + page + "】页数据");
				sb.append("?").append("apkey=").append("bdbaee0e-8ADD-a970-1937-d507af6a1118").append("&starttime=")
						.append(encodedStartDatetime).append("&span=").append(timeInterval + "").append("&page=").append(page)
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

			//增加1200秒时间
			startDatetime = DateUtil.dateFormate(
					DateUtil.addSeconds(DateUtil.getDateTime(startDatetime, new Date()), timeInterval),
					DateUtil.FULL_CHINESE_PATTERN);
		}
	}

	private void insertToTkOrderInput(List<N_tbk_order> n_tbk_order_list) {
		if (n_tbk_order_list != null && n_tbk_order_list.size() > 0) {
			for (N_tbk_order n_tbk_order : n_tbk_order_list) {
				TkOrderInput tkOrderInput = new TkOrderInput();
				tkOrderInput.setAccount(ConfigUtil.getString("alimama.account"));
				if("169978395".equals(n_tbk_order.getAdzone_id())){
					tkOrderInput.setAdId("176864894");
				}else{
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
				// 收入比例，该字段无实际用途，接口无返回该字段
				// tkOrderInput.setIncomeRate(incomeRate);
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

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		@SuppressWarnings("resource")
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((OrderFetchByInterfaceMoredayTask) ctx.getBean("orderFetchByInterfaceMoredayTask")).getTask();
	}
}
