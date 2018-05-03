package com.bt.om.web.controller.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.common.SysConst;
import com.bt.om.enums.ResultCode;
import com.bt.om.enums.SessionKey;
import com.bt.om.util.RequestUtil;
import com.bt.om.vo.web.ResultVo;
import com.bt.om.web.BasicController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 逛鱼搜索Controller
 */
@Controller
public class SearchController extends BasicController {
	@RequestMapping(value = "/search.html", method = RequestMethod.GET)
	public String search(Model model, HttpServletRequest request) {
		return "search/search";
	}

	// 图形验证码验证
	@RequestMapping(value = "/api/vcodevaild", method = RequestMethod.POST)
	@ResponseBody
	public Model vcodeValid(Model model, HttpServletRequest request, HttpServletResponse response) {
		ResultVo<String> result = new ResultVo<>();
		result.setCode(ResultCode.RESULT_SUCCESS.getCode());
		result.setResultDes("");
		model = new ExtendedModelMap();
		String code="";
		
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			code = obj.get("vcode").getAsString();
			// 验证码必须验证
			if (StringUtils.isEmpty(code)) {
				result.setResult("1"); // 验证码为空状体
				model.addAttribute(SysConst.RESULT_KEY, result);
				return model;
			}
		} catch (IOException e) {
			result.setCode(ResultCode.RESULT_FAILURE.getCode());
			result.setResultDes("系统繁忙，请稍后再试！");
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}				
		
		String sessionCode = request.getSession().getAttribute(SessionKey.SESSION_CODE.toString()) == null ? ""
				: request.getSession().getAttribute(SessionKey.SESSION_CODE.toString()).toString();

		// 验证码有效验证
		if (!code.equalsIgnoreCase(sessionCode)) {
			result.setResult("2"); // 验证码不一致
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}
		
		request.getSession().removeAttribute(SessionKey.SESSION_CODE.toString());

		result.setResult("0");// 验证码验证成功
		model.addAttribute(SysConst.RESULT_KEY, result);
		return model;
	}
}
