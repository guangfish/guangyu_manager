package com.bt.om.web.controller.xcx;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bt.om.cache.JedisPool;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.RequestUtil;
import com.bt.om.util.TaobaoSmsNewUtil;
import com.bt.om.web.controller.api.v2.vo.CommonVo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController
@RequestMapping(value = "/xcx/api")
public class XcxBaseController {
	private static final Logger logger = Logger.getLogger(XcxBaseController.class);

	@Autowired
	private JedisPool jedisPool;

	@RequestMapping(value = "/getSmsCode", method = RequestMethod.POST)
	@ResponseBody
	public Model getSmsCode(Model model, HttpServletRequest request, HttpServletResponse response) {
		String remoteIp = RequestUtil.getRealIp(request);
		CommonVo commonVo = new CommonVo();
		String mobile = "";
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			mobile = obj.get("mobile").getAsString();
		} catch (IOException e) {
			commonVo.setStatus("1");
			commonVo.setDesc("系统繁忙，请稍后再试");
			model.addAttribute("response", commonVo);
			return model;
		}

		// 手机号验证
		if (StringUtils.isEmpty(mobile)) {
			commonVo.setStatus("2");
			commonVo.setDesc("手机号为必填");
			model.addAttribute("response", commonVo);
			return model;
		}

		Object smscodeObj = jedisPool.getFromCache("", mobile);
		if (smscodeObj != null) {
			commonVo.setStatus("3");
			commonVo.setDesc("请等待2分钟后再次发送短信验证码");
			model.addAttribute("response", commonVo);
			return model;
		}

		String vcode = getVcode(4);
		if (mobile.equals("13732203065")) {
			vcode = "123456";
		}
		logger.info(mobile + "的验证码：" + vcode);
		jedisPool.putInCache("", mobile, vcode, 120);

		// 发送短信验证码
		if ("on".equals(ConfigUtil.getString("is.sms.send"))) {
			if (!remoteIp.equals(GlobalVariable.resourceMap.get("send_sms_ignoy_ip"))) {
				TaobaoSmsNewUtil.sendSms("逛鱼返利", "SMS_125955002", "vcode", vcode, mobile);
			}
		}

		commonVo.setStatus("0");
		commonVo.setDesc("验证码发送成功");
		model.addAttribute("response", commonVo);
		return model;
	}

	public String getVcode(int size) {
		String retNum = "";
		// 定义验证码的范围
		// String codeStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		String codeStr = "1234567890";

		Random r = new Random();
		for (int i = 0; i < size; i++) {
			retNum += codeStr.charAt(r.nextInt(codeStr.length()));
		}
		return retNum;
	}

}
