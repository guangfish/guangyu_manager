package com.bt.om.web.controller.app;

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

import com.adtime.common.lang.DateUtil;
import com.bt.om.cache.JedisPool;
import com.bt.om.entity.DrawCash;
import com.bt.om.entity.DrawCashOrder;
import com.bt.om.entity.Invitation;
import com.bt.om.entity.User;
import com.bt.om.entity.UserOrder;
import com.bt.om.entity.UserOrderTmp;
import com.bt.om.service.IDrawCashOrderService;
import com.bt.om.service.IDrawCashService;
import com.bt.om.service.IInvitationService;
import com.bt.om.service.IUserOrderService;
import com.bt.om.service.IUserOrderTmpService;
import com.bt.om.service.IUserService;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.MailUtil;
import com.bt.om.util.SecurityUtil1;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.api.v2.vo.CommonVo;
import com.bt.om.web.controller.api.v2.vo.OrderDrawVo;
import com.bt.om.web.controller.app.vo.ResultVo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import redis.clients.jedis.ShardedJedis;

/**
 * 列表、提现类Controller
 */
@Controller
@RequestMapping(value = "/app/api")
public class AppDrawController extends BasicController {
	@Autowired
	private IUserOrderService userOrderService;
	@Autowired
	private IInvitationService invitationService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IUserOrderTmpService userOrderTmpService;
	@Autowired
	private JedisPool jedisPool;
	@Autowired
	private IDrawCashService drawCashService;
	@Autowired
	private IDrawCashOrderService drawCashOrderService;

	// 查询订单列表
	@RequestMapping(value = "/orderlist", method = RequestMethod.POST)
	@ResponseBody
	public Model searchList(Model model, HttpServletRequest request, HttpServletResponse response) {
		ResultVo resultVo = new ResultVo();
		String userId = "";
		String orderStatus = "1";
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("userId") != null) {
				userId = obj.get("userId").getAsString();
				userId = SecurityUtil1.decrypts(userId);
			}
			if (obj.get("orderStatus") != null) {
				orderStatus = obj.get("orderStatus").getAsString();
			}
		} catch (IOException e) {
			resultVo.setStatus("1");
			resultVo.setDesc("系统繁忙，请稍后再试");
			model.addAttribute("response", resultVo);
			return model;
		}

		// 手机号码必须验证
		if (StringUtils.isEmpty(userId)) {
			resultVo.setStatus("2");
			resultVo.setDesc("请提交手机号码");
			model.addAttribute("response", resultVo);
			return model;
		}

		// 手机号码必须验证
		if (StringUtils.isEmpty(orderStatus)) {
			resultVo.setStatus("3");
			resultVo.setDesc("请提交订单状态");
			model.addAttribute("response", resultVo);
			return model;
		}

		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			List<UserOrder> userOrderList = userOrderService.selectAllOrderByMobile(userId);
			List<UserOrder> userOrderSettleList = new ArrayList<>();// 结算订单列表
			List<UserOrder> userOrderPayList = new ArrayList<>();// 付款订单列表
			List<UserOrder> userOrderNoValidList = new ArrayList<>();// 失效订单列表
			for (UserOrder userOrder : userOrderList) {
				if ("订单结算".equals(userOrder.getOrderStatus())) {
					userOrderSettleList.add(userOrder);
				} else if ("订单失效".equals(userOrder.getOrderStatus())) {
					userOrderNoValidList.add(userOrder);
				} else {
					userOrderPayList.add(userOrder);
				}
			}

			// 结算订单
			if ("1".equals(orderStatus)) {
				if (userOrderSettleList != null && userOrderSettleList.size() > 0) {
					for (UserOrder userOrder : userOrderSettleList) {
						HashMap<String, String> map = new HashMap<>();
						map.put("imgUrl", userOrder.getProductImgUrl() == null ? "" : userOrder.getProductImgUrl());
						map.put("productName", userOrder.getProductInfo());
						map.put("commission", ((!"订单结算".equals(userOrder.getOrderStatus()))
								? ("￥" + userOrder.getCommission3()) : "￥" + (userOrder.getCommission3())));

						map.put("multiple", "" + userOrder.getFanliMultiple());
						map.put("orderStatus", userOrder.getOrderStatus());
						map.put("orderTime", DateUtil.formatDate(userOrder.getCreateTime(), DateUtil.CHINESE_PATTERN));
						list.add(map);
					}
				}
			}
			// 付款订单
			else if ("2".equals(orderStatus)) {
				if (userOrderPayList != null && userOrderPayList.size() > 0) {
					for (UserOrder userOrder : userOrderPayList) {
						HashMap<String, String> map = new HashMap<>();
						map.put("imgUrl", userOrder.getProductImgUrl() == null ? "" : userOrder.getProductImgUrl());
						map.put("productName", userOrder.getProductInfo());
						map.put("commission", ((!"订单结算".equals(userOrder.getOrderStatus()))
								? ("￥" + userOrder.getCommission3()) : "￥" + (userOrder.getCommission3())));

						map.put("multiple", "" + userOrder.getFanliMultiple());
						map.put("orderStatus", userOrder.getOrderStatus());
						map.put("orderTime", DateUtil.formatDate(userOrder.getCreateTime(), DateUtil.CHINESE_PATTERN));
						list.add(map);
					}
				}
			}
			// 失效订单
			else if ("3".equals(orderStatus)) {
				if (userOrderNoValidList != null && userOrderNoValidList.size() > 0) {
					for (UserOrder userOrder : userOrderNoValidList) {
						HashMap<String, String> map = new HashMap<>();
						map.put("imgUrl", userOrder.getProductImgUrl() == null ? "" : userOrder.getProductImgUrl());
						map.put("productName", userOrder.getProductInfo());
						map.put("commission", ((!"订单结算".equals(userOrder.getOrderStatus()))
								? ("￥" + userOrder.getCommission3()) : "￥" + (userOrder.getCommission3())));

						map.put("multiple", "" + userOrder.getFanliMultiple());
						map.put("orderStatus", userOrder.getOrderStatus());
						map.put("orderTime", DateUtil.formatDate(userOrder.getCreateTime(), DateUtil.CHINESE_PATTERN));
						list.add(map);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		resultVo.setData(list);
		model.addAttribute("response", resultVo);
		return model;
	}

	// 查询好友列表
	@RequestMapping(value = "/friendlist", method = RequestMethod.POST)
	@ResponseBody
	public Model friendList(Model model, HttpServletRequest request, HttpServletResponse response) {
		ResultVo resultVo = new ResultVo();
		String userId = "";
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("userId") != null) {
				userId = obj.get("userId").getAsString();
				userId = SecurityUtil1.decrypts(userId);
			}
		} catch (IOException e) {
			resultVo.setStatus("1");
			resultVo.setDesc("系统繁忙，请稍后再试");
			model.addAttribute("response", resultVo);
			return model;
		}

		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		Invitation invitation = new Invitation();
		invitation.setBeInviterMobile(userId);
		List<Invitation> invitationList = invitationService.findByMobileFriend(invitation);
		if (invitationList != null && invitationList.size() > 0) {
			for (Invitation invit : invitationList) {
				HashMap<String, String> map = new HashMap<>();
				map.put("mobile", invit.getBeInviterMobile());
				map.put("status", invit.getStatus() == 1 ? "未激活" : "已激活");
				map.put("ifreward", invit.getReward() == 1 ? "未领取" : "已领取");
				map.put("rewardMoney", invit.getMoney() + ""); // 奖励金额
				map.put("inviteTime", DateUtil.formatDate(invit.getCreateTime(), DateUtil.CHINESE_PATTERN));// 邀请时间
				list.add(map);
			}
		}

		resultVo.setData(list);
		model.addAttribute("response", resultVo);
		return model;
	}

	// 平台订单奖励列表
	@RequestMapping(value = "/orderrewardlist", method = RequestMethod.POST)
	@ResponseBody
	public Model orderRewardList(Model model, HttpServletRequest request, HttpServletResponse response) {
		ResultVo resultVo = new ResultVo();
		String userId = "";
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("userId") != null) {
				userId = obj.get("userId").getAsString();
				userId = SecurityUtil1.decrypts(userId);
			}
		} catch (IOException e) {
			resultVo.setStatus("1");
			resultVo.setDesc("系统繁忙，请稍后再试");
			model.addAttribute("response", resultVo);
			return model;
		}

		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		User user = userService.selectByMobile(userId);
		List<UserOrder> userOrderList = userOrderService.selectByInviteCode(user.getMyInviteCode());

		if (userOrderList != null && userOrderList.size() > 0) {
			for (UserOrder userOrder : userOrderList) {
				HashMap<String, String> map = new HashMap<>();
				map.put("mobile", userOrder.getMobile());// 会员手机号
				map.put("commission", userOrder.getCommission3() + "");// 会员订单返现金额
				map.put("orderReward", userOrder.getCommissionReward() + "");// 订单奖励金额
				map.put("orderRewardRate", userOrder.getCommissionRewardRate() + ""); // 订单奖励金额百分比
				list.add(map);
			}
		}

		resultVo.setData(list);
		model.addAttribute("response", resultVo);
		return model;
	}

	// 订单保存
	@RequestMapping(value = "/ordersave", method = RequestMethod.POST)
	@ResponseBody
	public Model orderSave(Model model, HttpServletRequest request, HttpServletResponse response) {
		CommonVo commonVo = new CommonVo();
		String userId = "";
		String orderId = "";
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("userId") != null) {
				userId = obj.get("userId").getAsString();
				userId = SecurityUtil1.decrypts(userId);
			}
			if (obj.get("orderId") != null) {
				orderId = obj.get("orderId").getAsString();
			}

			// 手机号必须验证
			if (StringUtils.isEmpty(userId)) {
				commonVo.setStatus("1");
				commonVo.setDesc("请提交用户ID");
				model.addAttribute("response", commonVo);
				return model;
			}
			// 订单号必须验证
			if (StringUtils.isEmpty(orderId)) {
				commonVo.setStatus("2");
				commonVo.setDesc("请输入订单号");
				model.addAttribute("response", commonVo);
				return model;
			}
		} catch (Exception e) {
			commonVo.setStatus("3");
			commonVo.setDesc("系统繁忙，请稍后再试");
			model.addAttribute("response", commonVo);
			return model;
		}

		UserOrderTmp userOrderTmp = new UserOrderTmp();
		userOrderTmp.setOrderId(orderId);
		if (orderId.length() == 18) {
			userOrderTmp.setBelong(1);
		} else {
			userOrderTmp.setBelong(2);
		}
		userOrderTmp.setMobile(userId);
		userOrderTmp.setStatus(1);
		userOrderTmp.setCreateTime(new Date());
		userOrderTmp.setUpdateTime(new Date());
		try {
			userOrderTmpService.insert(userOrderTmp);
			commonVo.setStatus("0");
			commonVo.setDesc("订单保存成功");
		} catch (Exception e) {
			logger.info(e.getMessage());
			commonVo.setStatus("4");
			commonVo.setDesc("请勿重复提交订单号");
		}
		model.addAttribute("response", commonVo);
		return model;
	}

	// 申请提现
	@RequestMapping(value = "/draw", method = RequestMethod.POST)
	@ResponseBody
	public Model draw(Model model, HttpServletRequest request, HttpServletResponse response) {
		OrderDrawVo orderDrawVo = new OrderDrawVo();
		String userId = "";
		String code = "";
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
			if (obj.get("code") != null) {
				code = obj.get("code").getAsString();
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
		if (StringUtils.isEmpty(code)) {
			orderDrawVo.setStatus("3");
			orderDrawVo.setDesc("请求参数中缺少短信验证码");
			model.addAttribute("response", orderDrawVo);
			return model;
		}

		ShardedJedis jedis = jedisPool.getResource();
		String vcodejds = jedis.get(userId);
		// 短信验证码已过期
		if (StringUtils.isEmpty(vcodejds)) {
			orderDrawVo.setStatus("4");
			orderDrawVo.setDesc("短信验证码已过期，请重新获取");
			model.addAttribute("response", orderDrawVo);
			jedis.close();
			return model;
		}

		// 验证码有效验证
		if (!code.equalsIgnoreCase(vcodejds)) {
			orderDrawVo.setStatus("5");
			orderDrawVo.setDesc("短信验证码验证失败");
			model.addAttribute("response", orderDrawVo);
			jedis.close();
			return model;
		}

		jedis.del(userId);
		jedis.close();

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

		// 平台订单奖励
		double orderReward = 0d;
		List<UserOrder> userOrderRewardList = userOrderService.selectByInviteCode(user.getMyInviteCode());
		if (userOrderRewardList != null && userOrderRewardList.size() > 0) {
			for (UserOrder userOrder : userOrderRewardList) {
				orderReward = orderReward + userOrder.getCommissionReward();
			}
		}

		// 订单返现
		List<UserOrder> userOrderList = userOrderService.selectByMobile(userId);
		double totalCommission = 0;
		int productNums = 0;
		if (userOrderList != null && userOrderList.size() > 0) {
			productNums = userOrderList.size();
			for (UserOrder userOrder : userOrderList) {
				totalCommission = totalCommission + userOrder.getCommission3() * userOrder.getFanliMultiple();
			}
		}

		if (totalCommission <= 0 && orderReward <= 0 && reward <= 0) {
			orderDrawVo.setStatus("6");
			orderDrawVo.setDesc("可提现金额为0");
			model.addAttribute("response", orderDrawVo);
			return model;
		}

		DrawCash drawCash = new DrawCash();
		drawCash.setMobile(userId);
		drawCash.setAlipayAccount(user.getAlipay());
		drawCash.setStatus(1);
		drawCash.setCash(totalCommission);
		drawCash.setReward(reward);
		drawCash.setOrderReward(orderReward);
		drawCash.setCreateTime(new Date());
		drawCash.setUpdateTime(new Date());
		drawCashService.insert(drawCash);

		// 订单提现后插入提现订单表并更新订单状态
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

		// 订单奖励提现后更新订单奖励状态
		if (userOrderRewardList != null && userOrderRewardList.size() > 0) {
			for (UserOrder userOrder : userOrderRewardList) {
				userOrder.setRewardStatus(2);
				userOrder.setUpdateTime(new Date());
				userOrderService.updateRewardStatus(userOrder);
			}
		}

		// 邀请奖励提现后更新邀请奖励状态
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
		map.put("orderReward", orderReward + "");
		map.put("total", String.valueOf(((float) (Math.round(totalCommission * 100)) / 100) + reward + orderReward));
		orderDrawVo.setData(map);
		model.addAttribute("response", orderDrawVo);
		return model;
	}
}
