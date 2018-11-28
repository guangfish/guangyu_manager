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

import com.bt.om.entity.Invitation;
import com.bt.om.entity.SettleInfo;
import com.bt.om.entity.User;
import com.bt.om.entity.UserOrder;
import com.bt.om.mapper.InvitationMapper;
import com.bt.om.mapper.SettleInfoMapper;
import com.bt.om.mapper.UserMapper;
import com.bt.om.mapper.UserOrderMapper;
import com.bt.om.util.DateUtil;

/**
 * 
 * @author Lenovo 定期结算任务
 *
 */
@Component
public class SettleTask {
	private static final Logger logger = Logger.getLogger(SettleTask.class);
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserOrderMapper userOrderMapper;
	@Autowired
	private InvitationMapper invitationMapper;
	@Autowired
	private SettleInfoMapper settleInfoMapper;

	@Scheduled(cron = "0 0 0 28 * ?")
	public void settleTask() {
		logger.info("每月28日晚结算任务");
		try {
			List<User> userList = userMapper.selectAll();
			for (User user : userList) {
				SettleInfo settleInfo = new SettleInfo();
				Map<String, String> map = new HashMap<>();
				// 订单结算
				map.put("mobile", user.getMobile());
				map.put("orderTime", DateUtil.dateFormate(new Date(), "yyyy-MM") + "-01 00:00:00");
				List<UserOrder> orderFanliList = userOrderMapper.selectManualOrderFanli(map);
				double orderFanli = 0d;
				float orderFanliF = 0f;
				for (UserOrder userOrder : orderFanliList) {
					orderFanli = orderFanli + userOrder.getCommission3();
				}
				orderFanliF = ((float) (Math.round(orderFanli * 100)) / 100);
				System.out.println(user.getMobile() + "=" + orderFanliF);
				if (orderFanli > 0) {
					settleInfo.setMobile(user.getMobile());
					settleInfo.setType(1);
					settleInfo.setMoney(((float) (Math.round(orderFanli * 100)) / 100));
					settleInfo.setSettleTime(new Date());
					settleInfoMapper.insert(settleInfo);
				}
				// 更新订单状态为已结算状态
				for (UserOrder userOrder : orderFanliList) {
					userOrder.setSettleStatus(2);
					userOrder.setUpdateTime(new Date());
					userOrderMapper.updateByPrimaryKey(userOrder);
				}

				// 订单奖励
				settleInfo = new SettleInfo();
				map.clear();
				map.put("orderTime", DateUtil.dateFormate(new Date(), "yyyy-MM") + "-01 00:00:00");
				map.put("taInviteCode", user.getMyInviteCode());
				List<UserOrder> orderJiangliList = userOrderMapper.selectManualOrderJiangli(map);
				double orderJiangli = 0d;
				float orderJiangliF = 0f;
				for (UserOrder userOrder : orderJiangliList) {
					orderJiangli = orderJiangli + userOrder.getCommissionReward();
				}
				orderJiangliF = ((float) (Math.round(orderJiangli * 100)) / 100);
				System.out.println(user.getMobile() + "=" + orderJiangliF);
				if (orderJiangli > 0) {
					settleInfo.setMobile(user.getMobile());
					settleInfo.setType(2);
					settleInfo.setMoney(((float) (Math.round(orderJiangli * 100)) / 100));
					settleInfo.setSettleTime(new Date());
					settleInfoMapper.insert(settleInfo);
				}
				// 更新订单奖励状态为已提现状态
				for (UserOrder userOrder : orderJiangliList) {
					userOrder.setRewardStatus(2);
					userOrder.setUpdateTime(new Date());
					userOrderMapper.updateByPrimaryKey(userOrder);
				}

				// 邀请结算
				settleInfo = new SettleInfo();
				map.put("mobile", user.getMobile());
				List<Invitation> orderInviteliList = invitationMapper.selectManualInviteJiangli(map);
				float orderInviteli = 0;
				for (Invitation invitation : orderInviteliList) {
					orderInviteli = orderInviteli + invitation.getMoney();
				}
				logger.info(user.getMobile() + "=" + orderInviteli);
				if (orderInviteli > 0) {
					settleInfo.setMobile(user.getMobile());
					settleInfo.setType(3);
					settleInfo.setMoney(orderInviteli);
					settleInfo.setSettleTime(new Date());
					settleInfoMapper.insert(settleInfo);
				}
				for (Invitation invitation : orderInviteliList) {
					invitation.setReward(2);
					invitation.setUpdateTime(new Date());
					invitationMapper.updateByPrimaryKey(invitation);
				}

				// 账号余额结算
				float userBalance = orderFanliF + orderJiangliF + orderInviteli + user.getHongbao();
				user.setBalance(user.getBalance() + userBalance);
				user.setHongbao(0f);
				user.setUpdateTime(new Date());
				userMapper.updateByPrimaryKey(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((SettleTask) ctx.getBean("settleTask")).settleTask();
	}
}
