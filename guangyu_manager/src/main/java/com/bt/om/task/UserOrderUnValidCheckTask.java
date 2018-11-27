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

import com.bt.om.entity.TkOrderInput;
import com.bt.om.entity.TkOrderInputJd;
import com.bt.om.entity.UserOrder;
import com.bt.om.service.ITkOrderInputJdService;
import com.bt.om.service.ITkOrderInputService;
import com.bt.om.service.IUserOrderService;
import com.bt.om.system.GlobalVariable;

/**
 * 淘宝订单状态为“订单结算”，但是由于退货等原因需要再次核验订单
 */
//@Component
public class UserOrderUnValidCheckTask {
	private static final Logger logger = Logger.getLogger(UserOrderUnValidCheckTask.class);
	@Autowired
	private IUserOrderService userOrderService;
	@Autowired
	private ITkOrderInputService tkOrderInputService;
	@Autowired
	private ITkOrderInputJdService tkOrderInputJdService;

	// 每隔一段时间进行一次订单校验
	@Scheduled(cron = "0/30 * * * * ?")
	public void userOrderUnValidCheck() {
		String ifRun = GlobalVariable.resourceMap.get("UserOrderUnValidCheckTask");
		if ("1".equals(ifRun)) {
			logger.info("淘宝订单状态为“订单结算”，但是由于退货等原因需要再次核验订单");
			taobaoCheck();
			jdCheck();
		}		
	}
	
	private void taobaoCheck(){
		UserOrder userOrder = new UserOrder();
		userOrder.setStatus1(2);//已核对
		userOrder.setStatus2(1);
		userOrder.setStatus3(1);
		userOrder.setBelong(1);
		List<UserOrder> userOrderList = userOrderService.selectUnCheckOrderTaobao(userOrder);
		if (userOrderList != null && userOrderList.size() > 0) {
			logger.info("淘宝共有" + userOrderList.size() + "件商品为订单结算且未申请提现状态");
		    for(UserOrder userOrder1:userOrderList){
		    	Map<String, Object> map = new HashMap<>();
				map.put("productId", userOrder1.getProductId());
				map.put("orderId", userOrder1.getOrderId());
				//从淘宝导入的订单有可能会出现同一个订单号下面有多条相同商品记录
				List<TkOrderInput> tkOrderInputList = tkOrderInputService.selectByMap(map);
				if (tkOrderInputList != null && tkOrderInputList.size()>0) {
					//取第一个
					TkOrderInput tkOrderInput=tkOrderInputList.get(0);
					if (!tkOrderInput.getOrderStatus().equals(userOrder1.getOrderStatus())) {
						userOrder1.setStatus1(3);
						userOrder1.setOrderStatus("订单失效");
						userOrder1.setUpdateTime(new Date());
						userOrderService.updateByPrimaryKey(userOrder1);
						logger.info("淘宝订单"+userOrder1.getOrderId()+"状态更新为订单失效");
					}
				}else{
//					logger.info("订单" + userOrder1.getOrderId() + "未从阿里妈妈导入、或订单不存在");
				}
		    }
		}else{
			logger.info("淘宝所有商品已提现或无订单");
		}
	}
	
	private void jdCheck(){
		UserOrder userOrder = new UserOrder();
		userOrder.setStatus1(2);//已核对
		userOrder.setStatus2(1);
		userOrder.setStatus3(1);
		userOrder.setBelong(2);
		List<UserOrder> userOrderList = userOrderService.selectUnCheckOrderJd(userOrder);
		if (userOrderList != null && userOrderList.size() > 0) {
			logger.info("京东共有" + userOrderList.size() + "件商品为订单结算且未申请提现状态");
		    for(UserOrder userOrder1:userOrderList){
		    	Map<String, Object> map = new HashMap<>();
				map.put("productId", userOrder1.getProductId());
				map.put("orderId", userOrder1.getOrderId());				
				TkOrderInputJd tkOrderInputJd = tkOrderInputJdService.selectByMap(map);
				if (tkOrderInputJd != null) {
					if (tkOrderInputJd.getOrderStatus().contains("无效")) {
						userOrder1.setStatus1(3);
						userOrder1.setOrderStatus("订单失效");
						userOrder1.setUpdateTime(new Date());
						userOrderService.updateByPrimaryKey(userOrder1);
						logger.info("京东订单"+userOrder1.getOrderId()+"状态更新为订单失效");
					}
				}else{
					logger.info("订单" + userOrder1.getOrderId() + "未从京东导入、或订单不存在");
				}
		    }
		}else{
			logger.info("京东所有商品已提现或无订单");
		}
	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((UserOrderUnValidCheckTask) ctx.getBean("userOrderUnValidCheckTask")).userOrderUnValidCheck();
	}
}
