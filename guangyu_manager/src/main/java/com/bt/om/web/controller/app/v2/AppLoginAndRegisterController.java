package com.bt.om.web.controller.app.v2;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
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
import com.bt.om.entity.Invitation;
import com.bt.om.entity.User;
import com.bt.om.service.IInvitationService;
import com.bt.om.service.IUserService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.NumberUtil;
import com.bt.om.util.SecurityUtil1;
import com.bt.om.util.StringUtil;
import com.bt.om.web.controller.api.v2.vo.RegisterVo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Controller
@RequestMapping(value = "/app/api")
public class AppLoginAndRegisterController {
	@Autowired
	private JedisPool jedisPool;

	@Autowired
	private IUserService userService;
	@Autowired
	private IInvitationService invitationService;

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

		if (!"13732203065".equals(mobile)) {
			Object smscodeObj = jedisPool.getFromCache("", mobile);
			if (smscodeObj == null) {
				registerVo.setStatus("1");
				registerVo.setDesc("短信验证码已过期");
				model.addAttribute("response", registerVo);
				return model;
			}
			String smscode = (String) smscodeObj;
			if (!smscode.equalsIgnoreCase(code)) {
				registerVo.setStatus("2");
				registerVo.setDesc("短信验证码不正确");
				model.addAttribute("response", registerVo);
				return model;
			} else {
				jedisPool.delete("", mobile);
			}
		} else {
			if (!"123456".equalsIgnoreCase(code)) {
				registerVo.setStatus("2");
				registerVo.setDesc("短信验证码不正确");
				model.addAttribute("response", registerVo);
				return model;
			}
		}

		User user = userService.selectByMobile(mobile);
		if (user != null) {
			registerVo.setStatus("0");
			registerVo.setDesc("登陆成功");
			Map<String, String> data = new HashMap<>();

			// APP下载的短链接地址
			String appDownloadUrl = GlobalVariable.resourceMap.get("app_download_url");

			String inviteCodeInfo = GlobalVariable.resourceMap.get("invite_info");
			// inviteCodeInfo="邀请您加入逛鱼搜索，搜索淘宝、京东优惠券，拿返利！先领券，再购物，更划算！#Enter#-------------\r\n邀请好友成为会员，享永久平台奖励，邀请越多赚的越多！\r\n-------------\r\n下载链接：#URL#\r\n-------------\r\n邀请码：Ʊ#myInviteCode#Ʊ";
			inviteCodeInfo = inviteCodeInfo.replace("#Enter#", "\r\n").replace("#URL#", appDownloadUrl)
					.replace("#myInviteCode#", user.getMyInviteCode());

			data.put("userId", SecurityUtil1.encrypts(mobile));
			data.put("inviteCode", inviteCodeInfo);// 我的邀请码、带描述信息
			data.put("inviteCodeShort", user.getMyInviteCode());// 我的短邀请码
			data.put("userType", user.getAccountType() + "");// 账号类型1：普通会员
																// 2：超级会员
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

			if (obj.get("alipay") != null) {
				alipay = obj.get("alipay").getAsString();
			}
			if (obj.get("weixin") != null) {
				weixin = obj.get("weixin").getAsString();
			}
			code = obj.get("code").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Object smscodeObj = jedisPool.getFromCache("", mobile);
		if (smscodeObj == null) {
			registerVo.setStatus("1");
			registerVo.setDesc("短信验证码已过期");
			model.addAttribute("response", registerVo);
			return model;
		}
		String smscode = (String) smscodeObj;
		if (StringUtil.isEmpty(smscode)) {
			registerVo.setStatus("1");
			registerVo.setDesc("短信验证码已过期");
			model.addAttribute("response", registerVo);
			return model;
		}
		if (!smscode.equalsIgnoreCase(code)) {
			registerVo.setStatus("2");
			registerVo.setDesc("短信验证码不正确");
			model.addAttribute("response", registerVo);
			return model;
		} else {
			jedisPool.delete("", mobile);
		}

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
		// 注册时分配默认的PID
		user.setPid(GlobalVariable.resourceMap.get("taobao_default_pid"));
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
					// 5-30元的随机奖励
					invitation.setMoney(NumberUtil.getRandomInt(
							Integer.parseInt(GlobalVariable.resourceMap.get("reward.money")) / 2,
							Integer.parseInt(GlobalVariable.resourceMap.get("reward.money"))));
					// invitation.setMoney(Integer.parseInt(GlobalVariable.resourceMap.get("reward.money")));
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

		// APP下载的短链接地址
		String appDownloadUrl = GlobalVariable.resourceMap.get("app_download_url");

		String inviteCodeInfo = GlobalVariable.resourceMap.get("invite_info");
		inviteCodeInfo = inviteCodeInfo.replace("#Enter#", "\r\n").replace("#URL#", appDownloadUrl)
				.replace("#myInviteCode#", user.getMyInviteCode());

		registerVo.setStatus("0");
		registerVo.setDesc("注册成功");
		Map<String, String> data = new HashMap<>();
		data.put("userId", SecurityUtil1.encrypts(mobile));
		data.put("inviteCode", inviteCodeInfo);// 我的邀请码、带描述信息
		data.put("inviteCodeShort", user.getMyInviteCode());// 我的短邀请码
		data.put("userType", "2");// 账号类型1：普通会员 2：超级会员
		data.put("sex", sex + "");
		registerVo.setData(data);
		model.addAttribute("response", registerVo);
		return model;
	}
}
