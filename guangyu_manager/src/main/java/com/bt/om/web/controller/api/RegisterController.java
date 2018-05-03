package com.bt.om.web.controller.api;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.common.SysConst;
import com.bt.om.entity.SysUser;
import com.bt.om.entity.SysUserRole;
import com.bt.om.enums.ResultCode;
import com.bt.om.enums.SessionKey;
import com.bt.om.service.ISysUserRoleService;
import com.bt.om.service.ISysUserService;
import com.bt.om.vo.api.UserVo;
import com.bt.om.vo.web.ResultVo;
import com.bt.om.web.BasicController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 注册Controller
 */
@Controller
public class RegisterController extends BasicController {

	@Autowired
	private ISysUserService sysUserService;

	@Autowired
	private ISysUserRoleService sysUserRoleService;

	@RequestMapping(value = "/register.html", method = RequestMethod.GET)
	public String search(Model model, HttpServletRequest request) {
		return "search/register";
	}

	// 订单保存
	@RequestMapping(value = "/api/register", method = RequestMethod.POST)
	@ResponseBody
	public Model register(Model model, HttpServletRequest request, HttpServletResponse response) {
		ResultVo<UserVo> result = new ResultVo<>();
		result.setCode(ResultCode.RESULT_SUCCESS.getCode());
		result.setResultDes("");
		model = new ExtendedModelMap();
		String mobile = "";
		String password = "";
		String alipayAccount = "";
		String weixinAccount = "";
		String vcode = "";

		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			mobile = obj.get("mobile").getAsString();
			password = obj.get("password").getAsString();
			alipayAccount = obj.get("alipayaccount").getAsString();
			weixinAccount = obj.get("weixinaccount").getAsString();
			vcode = obj.get("vcode").getAsString();

			// 手机号必须验证
			if (StringUtils.isEmpty(mobile)) {
				result.setResult(new UserVo("", "1"));
				model.addAttribute(SysConst.RESULT_KEY, result);
				return model;
			}
			// 订单号必须验证
			if (StringUtils.isEmpty(password)) {
				result.setResult(new UserVo("", "2"));
				model.addAttribute(SysConst.RESULT_KEY, result);
				return model;
			}
			// 支付宝账号必须验证
			if (StringUtils.isEmpty(alipayAccount)) {
				result.setResult(new UserVo("", "3"));
				model.addAttribute(SysConst.RESULT_KEY, result);
				return model;
			}
			// 微信账号必须验证
			if (StringUtils.isEmpty(weixinAccount)) {
				result.setResult(new UserVo("", "4"));
				model.addAttribute(SysConst.RESULT_KEY, result);
				return model;
			}
			// 验证码必须验证
			if (StringUtils.isEmpty(vcode)) {
				result.setResult(new UserVo("", "5"));
				model.addAttribute(SysConst.RESULT_KEY, result);
				return model;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(ResultCode.RESULT_FAILURE.getCode());
			result.setResultDes("系统繁忙，请稍后再试！");
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}

		String sessionCode = request.getSession().getAttribute(SessionKey.SESSION_CODE.toString()) == null ? ""
				: request.getSession().getAttribute(SessionKey.SESSION_CODE.toString()).toString();

		// 验证码有效验证
		// if (!vcode.equalsIgnoreCase(sessionCode)) {
		// result.setResult("6"); // 验证码不一致
		// model.addAttribute(SysConst.RESULT_KEY, result);
		// return model;
		// }

		// request.getSession().removeAttribute(SessionKey.SESSION_CODE.toString());

		SysUser sysUser = new SysUser();
		sysUser.setUsername(mobile);
		sysUser.setPassword(password);
		sysUser.setUsertype(2);
		sysUser.setPlatform(1);
		sysUser.setCreateTime(new Date());
		sysUser.setUpdateTime(new Date());

		try {
			sysUserService.insert(sysUser);
			SysUserRole sysUserRole = new SysUserRole();
			sysUserRole.setPlatform(1);
			sysUserRole.setRoleId(101);
			sysUserRole.setUserId(sysUser.getId());
			sysUserRole.setCreateTime(new Date());
			sysUserRole.setUpdateTime(new Date());
			sysUserRoleService.insert(sysUserRole);

			result.setResult(new UserVo(String.valueOf(sysUser.getId()), "0"));// 用户注册成功
		} catch (Exception e) {
			e.printStackTrace();
			result.setResult(new UserVo("", "-1"));// 用户已注册
		}

		model.addAttribute(SysConst.RESULT_KEY, result);
		return model;
	}
}
