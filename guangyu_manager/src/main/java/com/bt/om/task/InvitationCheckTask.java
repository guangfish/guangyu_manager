package com.bt.om.task;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bt.om.entity.Invitation;
import com.bt.om.service.IInvitationService;

@Component
public class InvitationCheckTask {
	private static final Logger logger = Logger.getLogger(InvitationCheckTask.class);

	@Autowired
	private IInvitationService invitationService;

	// 每隔一段时间进行一次邀请用户的核实
	@Scheduled(cron = "0 0 */2 * * ?")
	public void userOrderCheck() {
		logger.info("邀请用户的核实");
		List<Invitation> invitationList = invitationService.selectUnValidInvitationList();
		if (invitationList != null && invitationList.size() > 0) {
			for (Invitation invitation : invitationList) {
				invitation.setStatus(2);
				invitation.setUpdateTime(new Date());
				invitationService.updateByPrimaryKey(invitation);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String[] cfgs = new String[] { "classpath:spring/applicationContext.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(cfgs);
		((InvitationCheckTask) ctx.getBean("userOrderCheckTask")).userOrderCheck();
	}
}
