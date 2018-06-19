package com.bt.om.web.controller.api.v2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.cache.JedisPool;
import com.bt.om.entity.DrawCash;
import com.bt.om.entity.Invitation;
import com.bt.om.entity.User;
import com.bt.om.entity.UserOrder;
import com.bt.om.service.IDrawCashService;
import com.bt.om.service.IInvitationService;
import com.bt.om.service.IUserOrderService;
import com.bt.om.service.IUserService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.DateUtil;
import com.bt.om.util.MailUtil;
import com.bt.om.util.SecurityUtil1;
import com.bt.om.util.StringUtil;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.api.v2.vo.OrderDrawVo;
import com.bt.om.web.util.CookieHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 我的Controller
 */
@Controller
@RequestMapping(value = "/v2")
public class MyControllerV2 extends BasicController {
	@Autowired
	private IUserService userService;
	@Autowired
	private IInvitationService invitationService;
	@Autowired
	private IDrawCashService drawCashService;
	@Autowired
	private IUserOrderService userOrderService;
	@Autowired
	private JedisPool jedisPool;

	@RequestMapping(value = "/my", method = { RequestMethod.GET, RequestMethod.POST })
	public String my(Model model, HttpServletRequest request) {
		String mobile = CookieHelper.getCookie("mobile");
		if (StringUtil.isEmpty(mobile)) {
			return "redirect:/v2/login?toUrl=/v2/my";
		} else {
			User user = userService.selectByMobile(mobile);
			model.addAttribute("user", user);
			Map<String, Object> map = new HashMap<>();
			map.put("mobile", mobile);
			map.put("status", 2);
			double cash = drawCashService.getSumByMobile(map);
			model.addAttribute("cash", cash);
			model.addAttribute("agencyRewardRate",
					Float.parseFloat(GlobalVariable.resourceMap.get("agency_reward_rate")) * 100);
			return "searchv2/my";
		}
	}

	@RequestMapping(value = "/myinvitation", method = { RequestMethod.GET, RequestMethod.POST })
	public String myinvitation(Model model, HttpServletRequest request) {
		String mobile = CookieHelper.getCookie("mobile");
		if (StringUtil.isEmpty(mobile)) {
			return "redirect:/v2/login?toUrl=/v2/myinvitation";
		} else {
			User user = userService.selectByMobile(mobile);
			model.addAttribute("user", user);
			Invitation invitation = new Invitation();
			invitation.setBeInviterMobile(mobile);
			List<Invitation> invitationList = invitationService.findByMobileFriend(invitation);
			model.addAttribute("invitationList", invitationList);
			int reward = 0;
			int activeFriend = 0;
			for (Invitation invit : invitationList) {
				if (invit.getStatus() == 2 && invit.getReward() == 1) {
					reward = reward + invit.getMoney();
					activeFriend = activeFriend + 1;
				}
			}
			model.addAttribute("reward", reward);
			model.addAttribute("activeFriend", activeFriend);

			return "searchv2/myinvitation";
		}
	}

	@RequestMapping(value = "/rewarddraw", method = RequestMethod.GET)
	public String rewarddraw(Model model, HttpServletRequest request) {
		String mobile = CookieHelper.getCookie("mobile");
		if (StringUtil.isEmpty(mobile)) {
			return "redirect:/v2/login?toUrl=/v2/rewarddraw";
		} else {
			User user = userService.selectByMobile(mobile);
			model.addAttribute("user", user);
			String weekday = DateUtil.getWeekOfDate(new Date());
			if ("2".equals(weekday) || "5".equals(weekday)) {
				return "searchv2/rewarddraw";
			} else {
				return "searchv2/rewarddraw";
			}
		}
	}

	@RequestMapping(value = "/agencyrewarddraw", method = RequestMethod.GET)
	public String agencyrewarddraw(Model model, HttpServletRequest request) {
		String mobile = CookieHelper.getCookie("mobile");
		if (StringUtil.isEmpty(mobile)) {
			return "redirect:/v2/login?toUrl=/v2/agencyrewarddraw";
		} else {
			User user = userService.selectByMobile(mobile);
			model.addAttribute("user", user);
			String weekday = DateUtil.getWeekOfDate(new Date());
			if ("2".equals(weekday) || "5".equals(weekday)) {
				return "searchv2/agencyrewarddraw";
			} else {
				return "searchv2/agencyrewarddraw";
			}
		}
	}

	@RequestMapping(value = "/agencyreward", method = RequestMethod.GET)
	public String agencyreward(Model model, HttpServletRequest request) {
		String mobile = CookieHelper.getCookie("mobile");
		if (StringUtil.isEmpty(mobile)) {
			return "redirect:/v2/login?toUrl=/v2/agencyreward";
		} else {
			User user = userService.selectByMobile(mobile);
			model.addAttribute("user", user);
			List<UserOrder> userOrderList = userOrderService.selectByInviteCode(user.getMyInviteCode());
			int orderNum = 0;
			double reward = 0f;
			if (userOrderList != null && userOrderList.size() > 0) {
				orderNum = userOrderList.size();
				for (UserOrder userOrder : userOrderList) {
					reward = reward + userOrder.getCommissionReward();
				}
			}
			model.addAttribute("orderNum", orderNum);
			model.addAttribute("reward", reward);
			model.addAttribute("userOrderList", userOrderList);
			return "searchv2/agencyreward";
		}
	}

	// 申请提现
	@RequestMapping(value = "/api/rewarddraw", method = RequestMethod.POST)
	@ResponseBody
	public OrderDrawVo rewardDraw(Model model, HttpServletRequest request, HttpServletResponse response) {
		OrderDrawVo orderDrawVo = new OrderDrawVo();
		String userId = "";
		String smscode = "";
		String type = "";
		User user = null;
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("type") != null) {
				type = obj.get("type").getAsString();
			}
			if (obj.get("userId") != null) {
				userId = obj.get("userId").getAsString();
				if (!"wap".equals(type)) {
					userId = SecurityUtil1.decrypts(userId);
				}
				user = userService.selectByMobile(userId);
			}
			if (obj.get("smsCode") != null) {
				smscode = obj.get("smsCode").getAsString();
			}
		} catch (IOException e) {
			orderDrawVo.setStatus("1");
			orderDrawVo.setDesc("系统繁忙，请稍后再试");
			return orderDrawVo;
		}

		// 手机号码必须验证
		if (StringUtils.isEmpty(userId)) {
			orderDrawVo.setStatus("2");
			orderDrawVo.setDesc("请求参数中缺少用户ID");
			return orderDrawVo;
		}
		// 短信验证码必须验证
		if (StringUtils.isEmpty(smscode)) {
			orderDrawVo.setStatus("3");
			orderDrawVo.setDesc("请求参数中缺少短信验证码");
			return orderDrawVo;
		}

		String vcodejds = jedisPool.getResource().get(userId);
		// 短信验证码已过期
		if (StringUtils.isEmpty(vcodejds)) {
			orderDrawVo.setStatus("4");
			orderDrawVo.setDesc("短信验证码已过期，请重新获取");
			return orderDrawVo;
		}

		// 验证码有效验证
		if (!smscode.equalsIgnoreCase(vcodejds)) {
			orderDrawVo.setStatus("5");
			orderDrawVo.setDesc("短信验证码验证失败");
			return orderDrawVo;
		}

		jedisPool.getResource().del(userId);

		// 查询奖励邀请及奖励金额
		Invitation invitationVo = new Invitation();
		invitationVo.setInviterMobile(userId);
		invitationVo.setStatus(2);
		invitationVo.setReward(1);
		List<Invitation> invitationList = invitationService.selectInvitationList(invitationVo);
		int reward = 0;
		if (invitationList != null && invitationList.size() > 0) {
			for (Invitation invitation : invitationList) {
				if (invitation.getStatus() == 2 && invitation.getReward() == 1) {
					reward = reward + invitation.getMoney();
				}
			}
		}

		DrawCash drawCash = new DrawCash();
		drawCash.setMobile(userId);
		drawCash.setAlipayAccount(user.getAlipay());
		drawCash.setStatus(1);
		drawCash.setCash(0d);
		drawCash.setReward(reward);
		drawCash.setCreateTime(new Date());
		drawCash.setUpdateTime(new Date());
		drawCashService.insert(drawCash);

		if (invitationList != null && invitationList.size() > 0) {
			for (Invitation invitation : invitationList) {
				invitation.setReward(2);
				invitation.setUpdateTime(new Date());
				invitationService.updateByPrimaryKeySelective(invitation);
			}
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				// 发送邮件通知有客户申请提现
				if ("on".equals(ConfigUtil.getString("monitor.email.send.status"))) {
					List<String> tos = new ArrayList<>();
					String mailToStr = ConfigUtil.getString("monitor.email.to");
					String[] mailTos = mailToStr.split(";");
					for (int i = 0; i < mailTos.length; i++) {
						tos.add(mailTos[i]);
					}
					logger.info("开始发送邮件通知");
					MailUtil.sendEmail("逛鱼返利", "用户发起提现申请，请及时处理", tos);
				}
			}
		}).start();

		orderDrawVo.setStatus("0");
		orderDrawVo.setDesc("奖励提取成功，请注意支付宝查收！");
		Map<String, String> map = new HashMap<>();
		map.put("reward", reward + "");
		orderDrawVo.setMap(map);
		return orderDrawVo;
	}

	// 申请提现
	@RequestMapping(value = "/api/agencyrewarddraw", method = RequestMethod.POST)
	@ResponseBody
	public OrderDrawVo agencyRewardDraw(Model model, HttpServletRequest request, HttpServletResponse response) {
		OrderDrawVo orderDrawVo = new OrderDrawVo();
		String userId = "";
		String smscode = "";
		String type = "";
		User user = null;
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("type") != null) {
				type = obj.get("type").getAsString();
			}
			if (obj.get("userId") != null) {
				userId = obj.get("userId").getAsString();
				if (!"wap".equals(type)) {
					userId = SecurityUtil1.decrypts(userId);
				}
				user = userService.selectByMobile(userId);
			}
			if (obj.get("smsCode") != null) {
				smscode = obj.get("smsCode").getAsString();
			}
		} catch (IOException e) {
			orderDrawVo.setStatus("1");
			orderDrawVo.setDesc("系统繁忙，请稍后再试");
			return orderDrawVo;
		}

		// 手机号码必须验证
		if (StringUtils.isEmpty(userId)) {
			orderDrawVo.setStatus("2");
			orderDrawVo.setDesc("请求参数中缺少用户ID");
			return orderDrawVo;
		}
		// 短信验证码必须验证
		if (StringUtils.isEmpty(smscode)) {
			orderDrawVo.setStatus("3");
			orderDrawVo.setDesc("请求参数中缺少短信验证码");
			return orderDrawVo;
		}

		String vcodejds = jedisPool.getResource().get(userId);
		// 短信验证码已过期
		if (StringUtils.isEmpty(vcodejds)) {
			orderDrawVo.setStatus("4");
			orderDrawVo.setDesc("短信验证码已过期，请重新获取");
			return orderDrawVo;
		}

		// 验证码有效验证
		if (!smscode.equalsIgnoreCase(vcodejds)) {
			orderDrawVo.setStatus("5");
			orderDrawVo.setDesc("短信验证码验证失败");
			return orderDrawVo;
		}

		jedisPool.getResource().del(userId);

		List<UserOrder> userOrderList = userOrderService.selectByInviteCode(user.getMyInviteCode());
		double reward = 0f;
		if (userOrderList != null && userOrderList.size() > 0) {
			for (UserOrder userOrder : userOrderList) {
				reward = reward + userOrder.getCommissionReward();
			}
		}

		DrawCash drawCash = new DrawCash();
		drawCash.setMobile(userId);
		drawCash.setAlipayAccount(user.getAlipay());
		drawCash.setStatus(1);
		drawCash.setCash(reward);
		drawCash.setReward(0);
		drawCash.setCreateTime(new Date());
		drawCash.setUpdateTime(new Date());
		drawCashService.insert(drawCash);

		if (userOrderList != null && userOrderList.size() > 0) {
			for (UserOrder userOrder : userOrderList) {
				userOrder.setRewardStatus(2);
				userOrder.setUpdateTime(new Date());
				userOrderService.updateRewardStatus(userOrder);
			}
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				// 发送邮件通知有客户申请提现
				if ("on".equals(ConfigUtil.getString("monitor.email.send.status"))) {
					List<String> tos = new ArrayList<>();
					String mailToStr = ConfigUtil.getString("monitor.email.to");
					String[] mailTos = mailToStr.split(";");
					for (int i = 0; i < mailTos.length; i++) {
						tos.add(mailTos[i]);
					}
					logger.info("开始发送邮件通知");
					MailUtil.sendEmail("逛鱼返利", "用户发起提现申请，请及时处理", tos);
				}
			}
		}).start();

		orderDrawVo.setStatus("0");
		orderDrawVo.setDesc("奖励提取成功，请注意支付宝查收！");
		Map<String, String> map = new HashMap<>();
		map.put("reward", reward + "");
		orderDrawVo.setMap(map);
		return orderDrawVo;
	}
}
