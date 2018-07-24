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

//import com.adtime.common.lang.StringUtil;
import com.bt.om.cache.JedisPool;
import com.bt.om.entity.Invitation;
import com.bt.om.entity.User;
import com.bt.om.entity.UserOrder;
import com.bt.om.service.IDrawCashService;
import com.bt.om.service.IInvitationService;
import com.bt.om.service.IUserOrderService;
import com.bt.om.service.IUserService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.DateUtil;
import com.bt.om.util.NumberUtil;
import com.bt.om.util.SecurityUtil1;
import com.bt.om.util.StringUtil;
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
		String app = "android";
		String mobile = "";
		String code = "";
		InputStream is;
		try {
			is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("app") != null) {
				app = obj.get("app").getAsString();
			}
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

			String downloadUrl = GlobalVariable.resourceMap.get("android_download_url");
			if ("android".equals(app)) { 
				downloadUrl = GlobalVariable.resourceMap.get("android_download_url");
			} else if ("ios".equals(app)) {
				downloadUrl = GlobalVariable.resourceMap.get("ios_download_url");
			}

			String inviteCodeInfo = GlobalVariable.resourceMap.get("invite_info");
			// inviteCodeInfo="邀请您加入逛鱼搜索，搜索淘宝、京东优惠券，拿返利！先领券，再购物，更划算！\r\n-------------\r\n邀请好友成为会员，享永久平台奖励，邀请越多赚的越多！\r\n-------------\r\n下载链接：#URL#\r\n-------------\r\n邀请码：Ʊ#myInviteCode#Ʊ";
			inviteCodeInfo = inviteCodeInfo.replace("#URL#", downloadUrl).replace("#myInviteCode#",
					user.getMyInviteCode());
			inviteCodeInfo=StringUtil.stringToUnicode(inviteCodeInfo);

			data.put("userId", SecurityUtil1.encrypts(mobile));
			data.put("inviteCode", inviteCodeInfo);// 我的邀请码
			data.put("userType", user.getAccountType() + "");// 账号类型1：普通会员  2：超级会员
			data.put("sex", user.getSex() + "");
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
		String app = "android";
		String inviteCode = "";
		String mobile = "";
		String alipay = "";
		String weixin = "";
		int sex = 1;
		String code = "";
		InputStream is;
		try {
			is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("app") != null) {
				app = obj.get("app").getAsString();
			}
			if (obj.get("sex") != null) {
				sex = obj.get("sex").getAsInt();
			}
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
		user.setSex(sex);
		String hongbaoMin = GlobalVariable.resourceMap.get("register_hongbao_min");
		String hongbaoMax = GlobalVariable.resourceMap.get("register_hongbao_max");
		user.setHongbao((float) (Math.round(
				NumberUtil.getRandomNumberFloat(Integer.parseInt(hongbaoMin), Integer.parseInt(hongbaoMax)) * 100))
				/ 100);

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

		String downloadUrl = GlobalVariable.resourceMap.get("android_download_url");
		if ("android".equals(app)) {
			downloadUrl = GlobalVariable.resourceMap.get("android_download_url");
		} else if ("ios".equals(app)) {
			downloadUrl = GlobalVariable.resourceMap.get("ios_download_url");
		}

		String inviteCodeInfo = GlobalVariable.resourceMap.get("invite_info");
		inviteCodeInfo = inviteCodeInfo.replace("#URL#", downloadUrl).replace("#myInviteCode#", user.getMyInviteCode());

		registerVo.setStatus("0");
		registerVo.setDesc("注册成功");
		Map<String, String> data = new HashMap<>();
		data.put("userId", SecurityUtil1.encrypts(mobile));
		data.put("inviteCode", inviteCodeInfo);// 我的邀请码
		data.put("userType", "2");// 账号类型1：普通会员 2：超级会员
		data.put("sex", sex + "");
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

		String canDraw = "true";
		String reason = "";
		String canDrawSwitch = GlobalVariable.resourceMap.get("can_draw_switch");
		if ("1".equals(canDrawSwitch)) {
			String day = DateUtil.dateFormate(new Date(), "dd");
			String canDrawDay = GlobalVariable.resourceMap.get("can_draw_day");
			if (canDrawDay.equals(day)) {
				canDraw = "true";
			} else {
				canDraw = "false";
				reason = "亲！每月" + canDrawDay + "日开启提现功能。";
			}
		}

		String tklSymbols = GlobalVariable.resourceMap.get("tkl.symbol");

		User user = userService.selectByMobile(userId);
		if (user != null) {
			registerVo.setStatus("0");
			registerVo.setDesc("信息获取成功");
			Map<String, String> data = new HashMap<>();

			// 邀请的好友
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

			// 可提现订单
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

			// 订单平台奖励
			List<UserOrder> userOrderList1 = userOrderService.selectByInviteCode(user.getMyInviteCode());
			double platformReward = 0f;
			if (userOrderList1 != null && userOrderList1.size() > 0) {
				for (UserOrder userOrder : userOrderList1) {
					platformReward = platformReward + userOrder.getCommissionReward();
				}
			}
			platformReward=((float) (Math.round(platformReward * 100)) / 100);

			float hongbao = user.getHongbao();

			double totalMoney = ((double) (Math.round((tCommission + inviteReward + platformReward) * 100)) / 100);
			if ("true".equals(canDraw)) {
				if (totalMoney == 0) {
					canDraw = "false";
					if (hongbao > 0) {
						reason = "亲！红包不能单独提现，等有返现或奖励时再提吧。";
					} else {
						reason = "我的钱包空空的！";
					}
				}
			}
			totalMoney=((double) (Math.round((totalMoney + hongbao) * 100)) / 100);
			data.put("totalMoney", totalMoney+ "");// 总共可提现金额
			data.put("orderMoney", tCommission + "");// 订单可提金额
			data.put("inviteReward", inviteReward + "");// 邀请奖励金额
			data.put("platformReward", NumberUtil.format(platformReward));// 平台订单奖励金额
			data.put("hongbao", user.getHongbao() + "");// 我的红包
			data.put("friendNum", friendNum + "");// 通过我的邀请码注册的好友数
			data.put("orderNum", canDrawOrderNum + "");// 可提现订单数
			data.put("totalBuySave", cash + "");// 累计购物已省
			// data.put("inviteCode", user.getMyInviteCode());// 我的邀请码
			// data.put("userType", user.getAccountType() + "");// 账号类型1：普通会员
			// 2：超级会员
			data.put("tklSymbols", tklSymbols); // 淘口令前后特殊符号
			data.put("canDraw", canDraw);// 是否可以提现 true/false
			data.put("reason", reason);// 不可提现原因

			registerVo.setData(data);
			model.addAttribute("response", registerVo);
			return model;
		} else {
			return null;
		}
	}
}
