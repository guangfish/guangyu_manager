package com.bt.om.web.controller.xcx;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bt.om.cache.JedisPool;
import com.bt.om.entity.User;
import com.bt.om.service.IUserService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.NumberUtil;
import com.bt.om.util.SecurityUtil1;
import com.bt.om.web.controller.api.v2.vo.RegisterVo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController
@RequestMapping(value = "/xcx/api")
public class XcxLoginAndRegisterController {
	private static final Logger logger = Logger.getLogger(XcxLoginAndRegisterController.class);
	@Autowired
	private JedisPool jedisPool;

	@Autowired
	private IUserService userService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public Model login(Model model, HttpServletRequest request, HttpServletResponse response) {
		RegisterVo registerVo = new RegisterVo();
		String mobile = "";
		String smsCode = "";
		InputStream is;
		try {
			is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			mobile = obj.get("mobile").getAsString();
			smsCode = obj.get("smsCode").getAsString();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		Object smscodeObj = jedisPool.getFromCache("", mobile);
		if (smscodeObj == null) {
			registerVo.setStatus("1");
			registerVo.setDesc("短信验证码已过期");
			registerVo.setData(new HashMap<>());
			model.addAttribute("response", registerVo);
			return model;
		}
		String smscode = (String) smscodeObj;
		if (!smscode.equalsIgnoreCase(smsCode)) {
			registerVo.setStatus("2");
			registerVo.setDesc("短信验证码不正确");
			registerVo.setData(new HashMap<>());
			model.addAttribute("response", registerVo);
			return model;
		}

		User user = userService.selectByMobile(mobile);
		if (user != null) {
			jedisPool.delete("", mobile);
			registerVo.setStatus("0");
			registerVo.setDesc("登陆成功");
			Map<String, String> data = new HashMap<>();
			data.put("userId", SecurityUtil1.encrypts(mobile));
			data.put("userType", user.getAccountType() + "");// 账号类型1：普通会员//
																// 2：超级会员
			registerVo.setData(data);
			model.addAttribute("response", registerVo);
			return model;
		} else {
			registerVo.setStatus("3");
			registerVo.setDesc("该手机号未注册");
			registerVo.setData(new HashMap<>());
			model.addAttribute("response", registerVo);
			return model;
		}
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public Model register(Model model, HttpServletRequest request, HttpServletResponse response) {
		RegisterVo registerVo = new RegisterVo();
		String mobile = "";
		String smsCode = "";
		InputStream is;
		try {
			is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			mobile = obj.get("mobile").getAsString();
			smsCode = obj.get("smsCode").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Object smscodeObj = jedisPool.getFromCache("", mobile);
		if (smscodeObj == null) {
			registerVo.setStatus("1");
			registerVo.setDesc("短信验证码已过期");
			registerVo.setData(new HashMap<>());
			model.addAttribute("response", registerVo);
			return model;
		}
		String smscode = (String) smscodeObj;
		if (!smscode.equalsIgnoreCase(smsCode)) {
			registerVo.setStatus("2");
			registerVo.setDesc("短信验证码不正确");
			registerVo.setData(new HashMap<>());
			model.addAttribute("response", registerVo);
			return model;
		}

		User user = new User();
		user.setMobile(mobile);
		user.setPassword("");
		user.setCreateTime(new Date());
		user.setUpdateTime(new Date());
		String myInviteCode = (String.valueOf(((mobile + "1qaz2wsx").hashCode()))).replace("-", "");
		user.setMyInviteCode(myInviteCode);
		user.setAccountType(2);
		user.setSex(1);
		String hongbaoMin = GlobalVariable.resourceMap.get("register_hongbao_min");
		String hongbaoMax = GlobalVariable.resourceMap.get("register_hongbao_max");
		user.setHongbao((float) (Math.round(
				NumberUtil.getRandomNumberFloat(Integer.parseInt(hongbaoMin), Integer.parseInt(hongbaoMax)) * 100))
				/ 100);
		try {
			userService.insert(user);
		} catch (Exception e) {
			e.printStackTrace();
			registerVo.setStatus("3");
			registerVo.setDesc("该用户已注册");
			registerVo.setData(new HashMap<>());
			model.addAttribute("response", registerVo);
			return model;
		}

		jedisPool.delete("", mobile);
		registerVo.setStatus("0");
		registerVo.setDesc("注册成功");
		Map<String, String> data = new HashMap<>();
		data.put("userId", SecurityUtil1.encrypts(mobile));
		data.put("userType", user.getAccountType()+"");// 账号类型1：普通会员 2：超级会员
		registerVo.setData(data);
		model.addAttribute("response", registerVo);
		return model;
	}

}
