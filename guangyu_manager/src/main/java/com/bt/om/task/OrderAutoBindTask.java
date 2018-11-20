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
import com.bt.om.entity.User;
import com.bt.om.entity.UserOrderTmp;
import com.bt.om.mapper.TkOrderInputMapper;
import com.bt.om.mapper.UserMapper;
import com.bt.om.mapper.UserOrderTmpMapper;

/**
 * 
 * @author Lenovo 淘宝订单自动绑定任务
 * 定时从tk_order_input表中查询数据，自动绑定订单，把未绑定的订单保存到user_order_tmp表中
 * 然后有用户订单匹配任务去生产用户订单信息 userOrderMatchTask
 *
 */
@Component
public class OrderAutoBindTask {
	private static final Logger logger = Logger.getLogger(OrderAutoBindTask.class);
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserOrderTmpMapper userOrderTmpMapper;
	@Autowired
	private TkOrderInputMapper tkOrderInputMapper;

	@Scheduled(cron = "0 0/8 * * * ?")
	public void orderAutoBindTask() {
		logger.info("淘宝订单自动绑定任务");
		try {
			List<TkOrderInput> tkOrderInputList = tkOrderInputMapper.selectAll();
			for (TkOrderInput tkOrderInput : tkOrderInputList) {
				String orderId = tkOrderInput.getOrderId();
				String taobaoId = getTaobaoId(orderId);
				String adId = tkOrderInput.getAdId();
				Map<String, String> map = new HashMap<>();
				map.put("taobaoId", taobaoId);
				map.put("pid", adId);
				User user = userMapper.selectByTaobaoIdAndPid(map);
				if (user != null) {
					UserOrderTmp userOrderTmp = new UserOrderTmp();
					userOrderTmp.setMobile(user.getMobile());
					userOrderTmp.setBelong(1);
					userOrderTmp.setOrderId(orderId);
					userOrderTmp.setCreateTime(new Date());
					userOrderTmp.setUpdateTime(new Date());
					userOrderTmp.setStatus(1);
					try {
						userOrderTmpMapper.insert(userOrderTmp);
					} catch (Exception e) {
						logger.error("订单号:" + orderId + "已存在");
					}
				} else {
					logger.info("通过订单号、广告位ID找不到用户。" + "订单号:" + orderId + " PID:" + adId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
