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
import com.bt.om.entity.DrawCashOrder;
import com.bt.om.entity.Invitation;
import com.bt.om.entity.User;
import com.bt.om.entity.UserOrder;
import com.bt.om.service.IDrawCashOrderService;
import com.bt.om.service.IDrawCashService;
import com.bt.om.service.IInvitationService;
import com.bt.om.service.IUserOrderService;
import com.bt.om.service.IUserService;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.DateUtil;
import com.bt.om.util.MailUtil;
import com.bt.om.util.SecurityUtil1;
import com.bt.om.util.StringUtil;
import com.bt.om.web.controller.api.v2.vo.OrderDrawVo;
import com.bt.om.web.BasicController;
import com.bt.om.web.util.CookieHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 逛鱼申请提现Controller
 */
@Controller
@RequestMapping(value = "/v2")
public class OrderDrawControllerV2 extends BasicController {
	@Autowired
	private IDrawCashService drawCashService;
	@Autowired
	private IDrawCashOrderService drawCashOrderService;
	@Autowired
	private IUserOrderService userOrderService;
	@Autowired
	private IInvitationService invitationService;
	@Autowired
	private IUserService userService;

	@Autowired
	private JedisPool jedisPool;

	@RequestMapping(value = "/orderdraw", method = RequestMethod.GET)
	public String orderdrawv2(Model model, HttpServletRequest request) {
		String mobile = CookieHelper.getCookie("mobile");
		if (StringUtil.isEmpty(mobile)) {
			return "redirect:/v2/login?toUrl=/v2/orderdraw";
		} else {
			User user = userService.selectByMobile(mobile);
			model.addAttribute("user", user);
			String weekday = DateUtil.getWeekOfDate(new Date());
			if ("2".equals(weekday) || "5".equals(weekday)) {
				return "searchv2/orderdraw";
			} else {
				return "searchv2/orderdraw";
			}
		}
	}

	// 申请提现
	@RequestMapping(value = "/api/orderdraw", method = RequestMethod.POST)
	@ResponseBody
	public Model orderDraw(Model model, HttpServletRequest request, HttpServletResponse response) {
		OrderDrawVo orderDrawVo = new OrderDrawVo();
		String userId = "";
		String smscode = "";
		User user = null;
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("userId") != null) {
				userId = obj.get("userId").getAsString();
				userId = SecurityUtil1.decrypts(userId);
				user = userService.selectByMobile(userId);
			}
			if (obj.get("smsCode") != null) {
				smscode = obj.get("smsCode").getAsString();
			}
		} catch (IOException e) {
			orderDrawVo.setStatus("1");
			orderDrawVo.setDesc("系统繁忙，请稍后再试");
			model.addAttribute("response", orderDrawVo);
			return model;
		}

		// 手机号码必须验证
		if (StringUtils.isEmpty(userId)) {
			orderDrawVo.setStatus("2");
			orderDrawVo.setDesc("请求参数中缺少用户ID");
			model.addAttribute("response", orderDrawVo);
			return model;
		}
		// 短信验证码必须验证
		if (StringUtils.isEmpty(smscode)) {
			orderDrawVo.setStatus("3");
			orderDrawVo.setDesc("请求参数中缺少短信验证码");
			model.addAttribute("response", orderDrawVo);
			return model;
		}

		String vcodejds = jedisPool.getResource().get(userId);
		// 短信验证码已过期
		if (StringUtils.isEmpty(vcodejds)) {
			orderDrawVo.setStatus("4");
			orderDrawVo.setDesc("短信验证码已过期，请重新获取");
			model.addAttribute("response", orderDrawVo);
			return model;
		}

		// 验证码有效验证
		if (!smscode.equalsIgnoreCase(vcodejds)) {
			orderDrawVo.setStatus("5");
			orderDrawVo.setDesc("短信验证码验证失败");
			model.addAttribute("response", orderDrawVo);
			return model;
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

		List<UserOrder> userOrderList = userOrderService.selectByMobile(userId);
		if (userOrderList == null || userOrderList.size() <= 0) {
			orderDrawVo.setStatus("6");
			orderDrawVo.setDesc("无可提现商品或者商品处于核对中");
			model.addAttribute("response", orderDrawVo);
			return model;
		}
		double totalCommission = 0;
		int productNums = userOrderList.size();
		for (UserOrder userOrder : userOrderList) {
			totalCommission = totalCommission + userOrder.getCommission3() * userOrder.getFanliMultiple();
		}

		DrawCash drawCash = new DrawCash();
		drawCash.setMobile(userId);
		drawCash.setAlipayAccount(user.getAlipay());
		drawCash.setStatus(1);
		drawCash.setCash(totalCommission);
		drawCash.setReward(reward);
		drawCash.setCreateTime(new Date());
		drawCash.setUpdateTime(new Date());
		drawCashService.insert(drawCash);

		for (UserOrder userOrder : userOrderList) {
			DrawCashOrder drawCashOrder = new DrawCashOrder();
			drawCashOrder.setOrderId(userOrder.getOrderId());
			drawCashOrder.setDrawCachId(drawCash.getId());
			drawCashOrder.setCreateTime(new Date());
			drawCashOrder.setUpdateTime(new Date());
			drawCashOrderService.insert(drawCashOrder);

			// 更新提现状态为“提现申请中”
			userOrder.setStatus2(2);
			userOrder.setUpdateTime(new Date());
			userOrderService.updateStatus2(userOrder);
		}

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
		orderDrawVo.setDesc("提现成功");
		Map<String, String> map = new HashMap<>();
		map.put("productNums", productNums + "");
		map.put("fanli", String.valueOf(((float) (Math.round(totalCommission * 100)) / 100)));
		map.put("reward", reward + "");
		map.put("total", String.valueOf(((float) (Math.round(totalCommission * 100)) / 100) + reward));
		orderDrawVo.setData(map);
		model.addAttribute("response", orderDrawVo);
		return model;
	}
}
