package com.bt.om.web.controller.api.v2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.adtime.common.lang.StringUtil;
import com.bt.om.cache.JedisPool;
import com.bt.om.common.SysConst;
import com.bt.om.entity.User;
import com.bt.om.enums.ResultCode;
import com.bt.om.service.IUserService;
import com.bt.om.vo.web.ResultVo;
import com.bt.om.web.BasicController;
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

	@RequestMapping(value = "/login", method = { RequestMethod.GET, RequestMethod.POST })
	public String login(Model model, HttpServletRequest request) {
		String toUrl=request.getParameter("toUrl");
		model.addAttribute("toUrl", toUrl);
		return "searchv2/login";
	}

	@RequestMapping(value = "/api/login", method = RequestMethod.POST)
	@ResponseBody
	public Model loginApi(Model model, HttpServletRequest request, HttpServletResponse response) {
		ResultVo<String> result = new ResultVo<>();
		result.setCode(ResultCode.RESULT_SUCCESS.getCode());
		result.setResultDes("");
		model = new ExtendedModelMap();
		String mobile = "";
		String code = "";
		InputStream is;
		try {
			is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			mobile = obj.get("mobile").getAsString();
			code=obj.get("code").getAsString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String smscode = jedisPool.getResource().get(mobile);
		if(StringUtil.isEmpty(smscode)){
			result.setResult("1");// 短信验证码已过期
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}
		if (!smscode.equalsIgnoreCase(code)) {
			result.setResult("2");// 短信验证码不正确
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		} else {
			jedisPool.getResource().del(mobile);
		}
		
		User user=userService.selectByMobile(mobile);
		if(user!=null){
			result.setResult("0");// 登陆成功
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}else{
			result.setResult("3");// 该手机号未注册
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}
	}

	@RequestMapping(value = "/register", method = { RequestMethod.GET, RequestMethod.POST })
	public String register(Model model, HttpServletRequest request) {
		String toUrl=request.getParameter("toUrl");
		model.addAttribute("toUrl", toUrl);
		return "searchv2/register";
	}

	@RequestMapping(value = "/api/register", method = RequestMethod.POST)
	@ResponseBody
	public Model registerApi(Model model, HttpServletRequest request, HttpServletResponse response) {
		ResultVo<String> result = new ResultVo<>();
		result.setCode(ResultCode.RESULT_SUCCESS.getCode());
		result.setResultDes("");
		model = new ExtendedModelMap();
		String mobile = "";
		String alipay = "";
		String weixin = "";
		String code = "";
		String newpass = "";
		InputStream is;
		try {
			is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			mobile = obj.get("mobile").getAsString();
			alipay = obj.get("alipay").getAsString();
			weixin = obj.get("weixin").getAsString();
			code = obj.get("code").getAsString();
			newpass = obj.get("newpass").getAsString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String smscode = jedisPool.getResource().get(mobile);
		if(StringUtil.isEmpty(smscode)){
			result.setResult("2");// 短信验证码已过期
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}
		if (!smscode.equalsIgnoreCase(code)) {
			result.setResult("3");// 短信验证码不正确
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		} else {
			jedisPool.getResource().del(mobile);
		}

		User user = new User();
		user.setMobile(mobile);
		user.setPassword(newpass);
		user.setAlipay(alipay);
		user.setWeixin(weixin);
		user.setCreateTime(new Date());
		user.setUpdateTime(new Date());
		try {
			userService.insert(user);
		} catch (Exception e) {
			result.setResult("4");// 用户已注册
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}

		result.setResult("0");// 注册成功
		model.addAttribute(SysConst.RESULT_KEY, result);
		return model;
	}
}
