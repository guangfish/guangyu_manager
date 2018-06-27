package com.bt.om.web.controller.api.v2;

import java.io.IOException;
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

import com.adtime.common.lang.StringUtil;
import com.bt.om.cache.JedisPool;
import com.bt.om.common.SysConst;
import com.bt.om.entity.Invitation;
import com.bt.om.entity.User;
import com.bt.om.service.IInvitationService;
import com.bt.om.service.IUserService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.SecurityUtil1;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.api.v2.vo.CommonVo;
import com.bt.om.web.controller.api.v2.vo.RegisterVo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 登陆Controller
 */
@Controller
@RequestMapping(value = "/v2")
public class LoginControllerV2 extends BasicController {
	@Autowired
	private JedisPool jedisPool;

	@Autowired
	private IUserService userService;
	@Autowired
	private IInvitationService invitationService;

	@RequestMapping(value = "/login", method = { RequestMethod.GET, RequestMethod.POST })
	public String login(Model model, HttpServletRequest request) {
		String toUrl = request.getParameter("toUrl");
		model.addAttribute("toUrl", toUrl);
		return "searchv2/login";
	}

	@RequestMapping(value = "/api/login", method = RequestMethod.POST)
	@ResponseBody
	public Model loginApi(Model model, HttpServletRequest request, HttpServletResponse response) {
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
		} catch (IOException e) {
			e.printStackTrace();
		}

		String smscode = jedisPool.getResource().get(mobile);
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
			jedisPool.getResource().del(mobile);
		}

		User user = userService.selectByMobile(mobile);
		if (user != null) {						
			registerVo.setStatus("0");
			registerVo.setDesc("登陆成功");
			Map<String,String> data=new HashMap<>();
			data.put("userId", SecurityUtil1.encrypts(mobile));
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

	@RequestMapping(value = "/register", method = { RequestMethod.GET, RequestMethod.POST })
	public String register(Model model, HttpServletRequest request) {
		String toUrl = request.getParameter("toUrl");
		model.addAttribute("toUrl", toUrl);
		return "searchv2/register";
	}

	@RequestMapping(value = "/api/register", method = RequestMethod.POST)
	@ResponseBody
	public Model registerApi(Model model, HttpServletRequest request, HttpServletResponse response) {
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
		} catch (IOException e) {
			e.printStackTrace();
		}

		String smscode = jedisPool.getResource().get(mobile);
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
			jedisPool.getResource().del(mobile);
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
		try {
			userService.insert(user);
			if (StringUtil.isNotEmpty(inviteCode)) {
				User user1 = userService.selectByTaInviteCode(inviteCode);
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

		} catch (Exception e) {
			registerVo.setStatus("3");
			registerVo.setDesc("用户已注册");
			model.addAttribute("response", registerVo);
			return model;
		}

		registerVo.setStatus("0");
		registerVo.setDesc("注册成功");
		Map<String,String> data=new HashMap<>();
		data.put("userId", SecurityUtil1.encrypts(mobile));
		registerVo.setData(data);
		model.addAttribute("response", registerVo);
		return model;
	}
}
