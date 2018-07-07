package com.bt.om.web.controller.app;

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

import com.adtime.common.lang.StringUtil;
import com.bt.om.cache.JedisPool;
import com.bt.om.entity.Invitation;
import com.bt.om.entity.User;
import com.bt.om.entity.UserOrder;
import com.bt.om.service.IDrawCashService;
import com.bt.om.service.IInvitationService;
import com.bt.om.service.IUserOrderService;
import com.bt.om.service.IUserService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.SecurityUtil1;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.api.v2.vo.RegisterVo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import redis.clients.jedis.ShardedJedis;

/**
 * APP登陆、注册Controller
 */
@Controller
@RequestMapping(value = "/app/api")
public class AppLoginController extends BasicController {
	@Autowired
	private JedisPool jedisPool;

	@Autowired
	private IUserService userService;
	@Autowired
	private IInvitationService invitationService;
	@Autowired
	private IUserOrderService userOrderService;
	@Autowired
	private IDrawCashService drawCashService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public Model login(Model model, HttpServletRequest request, HttpServletResponse response) {
		RegisterVo registerVo = new RegisterVo();
		String mobile = "";
		String code = "";
		InputStream is;
		try {
			is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			mobile = obj.get("mobile").getAsString();
			code = obj.get("code").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ShardedJedis jedis = jedisPool.getResource();
		String smscode = jedis.get(mobile);
		if (StringUtil.isEmpty(smscode)) {
			registerVo.setStatus("1");
			registerVo.setDesc("短信验证码已过期");
			model.addAttribute("response", registerVo);
			jedis.close();
			return model;
		}
		if (!smscode.equalsIgnoreCase(code)) {
			registerVo.setStatus("2");
			registerVo.setDesc("短信验证码不正确");
			model.addAttribute("response", registerVo);
			jedis.close();
			return model;
		} else {
			jedis.del(mobile);
		}
		jedis.close();

		User user = userService.selectByMobile(mobile);
		if (user != null) {
			registerVo.setStatus("0");
			registerVo.setDesc("登陆成功");
			Map<String, String> data = new HashMap<>();

//			//邀请的好友
//			Invitation invitationVo = new Invitation();
//			invitationVo.setInviterMobile(mobile);
//			List<Invitation> invitationList = invitationService.selectInvitationList(invitationVo);
//			float inviteReward = 0;
//			int friendNum = 0;
//			int friendNumValid = 0;
//			int friendNumNoValid = 0;
//			float rewardAll = 0;
//			if (invitationList != null && invitationList.size() > 0) {
//				for (Invitation invitation : invitationList) {
//					// 邀请已激活获得奖励
//					if (invitation.getStatus() == 2 && invitation.getReward() == 1) {
//						friendNumValid = friendNumValid + 1;
//						inviteReward = inviteReward + invitation.getMoney();
//					}
//					// 邀请未激活，预计可获得奖励
//					if (invitation.getStatus() == 1) {
//						friendNumNoValid = friendNumNoValid + 1;
//						rewardAll = rewardAll + invitation.getMoney();
//					}
//				}
//				friendNum = invitationList.size();
//			}

//			//可提现订单
//			int canDrawOrderNum = 0;
//			double totalCommission = 0;
//			float tCommission = 0;
//			List<UserOrder> userOrderList = userOrderService.selectAllOrderByMobile(mobile);
//			List<UserOrder> userOrderCanDrawList = new ArrayList<>();
//			for (UserOrder userOrder : userOrderList) {
//				if ("订单结算".equals(userOrder.getOrderStatus())) {
//					canDrawOrderNum = canDrawOrderNum + 1;
//					totalCommission = totalCommission + userOrder.getCommission3() * userOrder.getFanliMultiple();
//					userOrderCanDrawList.add(userOrder);
//				}
//			}
//			tCommission = ((float) (Math.round(totalCommission * 100)) / 100);

//			// 累计购物已省
//			Map<String, Object> map = new HashMap<>();
//			map.put("mobile", mobile);
//			map.put("status", 2);
//			double cash = drawCashService.getSumByMobile(map);

//			//订单平台奖励
//			List<UserOrder> userOrderList1 = userOrderService.selectByInviteCode(user.getMyInviteCode());
//			double platformReward = 0f;
//			if (userOrderList1 != null && userOrderList1.size() > 0) {
//				for (UserOrder userOrder : userOrderList1) {
//					platformReward = platformReward + userOrder.getCommissionReward();
//				}
//			}

//			double totalMoney = ((double) (Math.round((tCommission + inviteReward + platformReward) * 100)) / 100);
			data.put("userId", SecurityUtil1.encrypts(mobile));
//			data.put("totalMoney", totalMoney + "");// 总共可提现金额
//			data.put("orderMoney", tCommission + "");// 订单可提金额
//			data.put("inviteReward", inviteReward + "");// 邀请奖励金额
//			data.put("platformReward", platformReward + "");// 平台订单奖励金额
//			data.put("friendNum", friendNum + "");// 通过我的邀请码注册的好友数
//			data.put("orderNum", canDrawOrderNum + "");// 可提现订单数
//			data.put("totalBuySave", cash + "");// 累计购物已省
			data.put("inviteCode", "邀请您加入逛鱼搜索，搜索淘宝、京东优惠券，拿返利！先领券，再购物，更划算！\r-------------\r邀请好友成为会员，享永久平台奖励，邀请越多赚的越多！\r-------------\r访问链接：https://www.guangfish.com\r-------------\r邀请码【"+user.getMyInviteCode()+"】");// 我的邀请码
			data.put("userType", user.getAccountType() + "");// 账号类型1：普通会员
//																// 2：超级会员
			registerVo.setData(data);
			model.addAttribute("response", registerVo);
			return model;
		} else {
			registerVo.setStatus("3");
			registerVo.setDesc("该手机号未注册");
			model.addAttribute("response", registerVo);
			return model;
		}
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public Model register(Model model, HttpServletRequest request, HttpServletResponse response) {
		RegisterVo registerVo = new RegisterVo();
		String inviteCode = "";
		String mobile = "";
		String alipay = "";
		String weixin = "";
		String code = "";
		InputStream is;
		try {
			is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("inviteCode") != null) {
				inviteCode = obj.get("inviteCode").getAsString();
			}
			mobile = obj.get("mobile").getAsString();
			alipay = obj.get("alipay").getAsString();
			weixin = obj.get("weixin").getAsString();
			code = obj.get("code").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ShardedJedis jedis = jedisPool.getResource();
		String smscode = jedis.get(mobile);
		if (StringUtil.isEmpty(smscode)) {
			registerVo.setStatus("1");
			registerVo.setDesc("短信验证码已过期");
			model.addAttribute("response", registerVo);
			jedis.close();
			return model;
		}
		if (!smscode.equalsIgnoreCase(code)) {
			registerVo.setStatus("2");
			registerVo.setDesc("短信验证码不正确");
			model.addAttribute("response", registerVo);
			jedis.close();
			return model;
		} else {
			jedis.del(mobile);
		}
		jedis.close();

		User user = new User();
		user.setMobile(mobile);
		user.setPassword("");
		user.setAlipay(alipay);
		user.setWeixin(weixin);
		user.setCreateTime(new Date());
		user.setUpdateTime(new Date());
		String myInviteCode = (String.valueOf(((mobile + "1qaz2wsx").hashCode()))).replace("-", "");
		user.setTaInviteCode(inviteCode);
		user.setMyInviteCode(myInviteCode);
		user.setAccountType(2);

		try {
			userService.insert(user);
			if (StringUtil.isNotEmpty(inviteCode)) {
				User user1 = userService.selectByTaInviteCode(inviteCode);
				if (user1 != null) {
					Invitation invitation = new Invitation();
					invitation.setInviterMobile(user1.getMobile());
					invitation.setBeInviterMobile(mobile);
					invitation.setStatus(1);
					invitation.setReward(1);
					invitation.setMoney(Integer.parseInt(GlobalVariable.resourceMap.get("reward.money")));
					invitation.setCreateTime(new Date());
					invitation.setUpdateTime(new Date());

					String mobile1 = invitationService.haveInvitation(invitation);
					if (StringUtils.isEmpty(mobile1)) {
						invitationService.insert(invitation);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			registerVo.setStatus("3");
			registerVo.setDesc("该用户已注册");
			model.addAttribute("response", registerVo);
			return model;
		}

		registerVo.setStatus("0");
		registerVo.setDesc("注册成功");
		Map<String, String> data = new HashMap<>();
		data.put("userId", SecurityUtil1.encrypts(mobile));
//		data.put("totalMoney", "0.0");// 总共可提现金额
//		data.put("orderMoney", "0.0");// 订单可提金额
//		data.put("inviteReward", "0.0");// 邀请奖励金额
//		data.put("platformReward", "0.0");// 平台订单奖励金额
//		data.put("friendNum", "0");// 好友数
//		data.put("orderNum", "0");// 可提现订单数
//		data.put("totalBuySave", "0.0");// 累计购物已省
		data.put("inviteCode", "邀请您加入逛鱼搜索，搜索淘宝、京东优惠券，拿返利！先领券，再购物，更划算！\r-------------\r邀请好友成为会员，享永久平台奖励，邀请越多赚的越多！\r-------------\r访问链接：https://www.guangfish.com\r-------------\r邀请码【"+myInviteCode+"】");// 我的邀请码
		data.put("userType", "2");// 账号类型1：普通会员 2：超级会员
		registerVo.setData(data);
		model.addAttribute("response", registerVo);
		return model;
	}
	
	@RequestMapping(value = "/drawstats", method = RequestMethod.POST)
	@ResponseBody
	public Model userInfo(Model model, HttpServletRequest request, HttpServletResponse response) {
		RegisterVo registerVo = new RegisterVo();
		String userId = "";	
		InputStream is;
		try {
			is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("userId") != null) {
				userId = obj.get("userId").getAsString();
				userId = SecurityUtil1.decrypts(userId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		

		User user = userService.selectByMobile(userId);
		if (user != null) {
			registerVo.setStatus("0");
			registerVo.setDesc("信息获取成功");
			Map<String, String> data = new HashMap<>();

			//邀请的好友
			Invitation invitationVo = new Invitation();
			invitationVo.setInviterMobile(userId);
			List<Invitation> invitationList = invitationService.selectInvitationList(invitationVo);
			float inviteReward = 0;
			int friendNum = 0;
			int friendNumValid = 0;
			int friendNumNoValid = 0;
			float rewardAll = 0;
			if (invitationList != null && invitationList.size() > 0) {
				for (Invitation invitation : invitationList) {
					// 邀请已激活获得奖励
					if (invitation.getStatus() == 2 && invitation.getReward() == 1) {
						friendNumValid = friendNumValid + 1;
						inviteReward = inviteReward + invitation.getMoney();
					}
					// 邀请未激活，预计可获得奖励
					if (invitation.getStatus() == 1) {
						friendNumNoValid = friendNumNoValid + 1;
						rewardAll = rewardAll + invitation.getMoney();
					}
				}
				friendNum = invitationList.size();
			}

			//可提现订单
			int canDrawOrderNum = 0;
			double totalCommission = 0;
			float tCommission = 0;
			List<UserOrder> userOrderList = userOrderService.selectAllOrderByMobile(userId);
			List<UserOrder> userOrderCanDrawList = new ArrayList<>();
			for (UserOrder userOrder : userOrderList) {
				if ("订单结算".equals(userOrder.getOrderStatus())) {
					canDrawOrderNum = canDrawOrderNum + 1;
					totalCommission = totalCommission + userOrder.getCommission3() * userOrder.getFanliMultiple();
					userOrderCanDrawList.add(userOrder);
				}
			}
			tCommission = ((float) (Math.round(totalCommission * 100)) / 100);

			// 累计购物已省
			Map<String, Object> map = new HashMap<>();
			map.put("mobile", userId);
			map.put("status", 2);
			double cash = drawCashService.getSumByMobile(map);

			//订单平台奖励
			List<UserOrder> userOrderList1 = userOrderService.selectByInviteCode(user.getMyInviteCode());
			double platformReward = 0f;
			if (userOrderList1 != null && userOrderList1.size() > 0) {
				for (UserOrder userOrder : userOrderList1) {
					platformReward = platformReward + userOrder.getCommissionReward();
				}
			}

			double totalMoney = ((double) (Math.round((tCommission + inviteReward + platformReward) * 100)) / 100);
//			data.put("userId", SecurityUtil1.encrypts(userId));
			data.put("totalMoney", totalMoney + "");// 总共可提现金额
			data.put("orderMoney", tCommission + "");// 订单可提金额
			data.put("inviteReward", inviteReward + "");// 邀请奖励金额
			data.put("platformReward", platformReward + "");// 平台订单奖励金额
			data.put("friendNum", friendNum + "");// 通过我的邀请码注册的好友数
			data.put("orderNum", canDrawOrderNum + "");// 可提现订单数
			data.put("totalBuySave", cash + "");// 累计购物已省
//			data.put("inviteCode", user.getMyInviteCode());// 我的邀请码
//			data.put("userType", user.getAccountType() + "");// 账号类型1：普通会员
																// 2：超级会员
			registerVo.setData(data);
			model.addAttribute("response", registerVo);
			return model;
		} else{
			return null;
		}
	}
}