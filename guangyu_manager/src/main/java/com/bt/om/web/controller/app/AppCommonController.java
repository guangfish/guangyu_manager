package com.bt.om.web.controller.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
import com.bt.om.entity.AppDownload;
import com.bt.om.service.IAppDownloadService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.RequestUtil;
import com.bt.om.util.SecurityUtil1;
import com.bt.om.util.TaobaoSmsNewUtil;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.api.v2.vo.AppDownloadVo;
import com.bt.om.web.controller.api.v2.vo.CommonVo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import redis.clients.jedis.ShardedJedis;

/**
 * 通用Controller
 */
@Controller
@RequestMapping(value = "/app/api")
public class AppCommonController extends BasicController {
	@Autowired
	private JedisPool jedisPool;
	@Autowired
	private IAppDownloadService appDownloadService;

	// 获取验证码
	@RequestMapping(value = "/getSmsCode", method = RequestMethod.POST)
	@ResponseBody
	public Model getSmsCode(Model model, HttpServletRequest request, HttpServletResponse response) {
		String remoteIp = RequestUtil.getRealIp(request);
		CommonVo commonVo = new CommonVo();
		String mobile = "";
		String userId = "";
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("userId") != null) {
				userId = obj.get("userId").getAsString();
				userId = SecurityUtil1.decrypts(userId);
				mobile = userId;
			} else {
				mobile = obj.get("mobile").getAsString();
			}
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

		ShardedJedis jedis = jedisPool.getResource();
		if (jedis.exists(mobile)) {
			commonVo.setStatus("3");
			commonVo.setDesc("请等待2分钟后再次发送短信验证码");
			model.addAttribute("response", commonVo);
			return model;
		}

		String vcode = getVcode(4);
		System.out.println(vcode);
		jedis.setex(mobile, 120, vcode);
		jedis.close();

		// 发送短信验证码
		if ("on".equals(ConfigUtil.getString("is.sms.send"))) {
			if (!remoteIp.equals(GlobalVariable.resourceMap.get("send_sms_ignoy_ip"))) {
				TaobaoSmsNewUtil.sendSms("逛鱼返利", "SMS_125955002", "vcode", vcode, mobile);
			}
		}

		commonVo.setStatus("0");
		commonVo.setDesc("验证码发送成功");

		response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");
		model.addAttribute("response", commonVo);
		return model;
	}
	
	@RequestMapping(value = "/download", method = RequestMethod.POST)
	@ResponseBody
	public Model download(Model model, HttpServletRequest request, HttpServletResponse response) {
		AppDownloadVo appDownloadVo = new AppDownloadVo();
		InputStream is;
		int version = 1;
		try {
			is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			version = obj.get("version").getAsInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		AppDownload appDownload = appDownloadService.selectLastest(version);
		Map<String, String> map = new HashMap<>();
		if (appDownload != null) {			
			map.put("link", appDownload.getAddress());
			map.put("version", appDownload.getVersion() + "");
			map.put("ifForce", appDownload.getIfForce()+"");
			appDownloadVo.setStatus("0");
			appDownloadVo.setDesc("获取新版本成功");
			appDownloadVo.setData(map);
		} else {
			appDownloadVo.setStatus("0");
			appDownloadVo.setDesc("已经是最新版本了");
			appDownloadVo.setData(map);
		}
		model.addAttribute("response", appDownloadVo);
		return model;
	}
	
	@RequestMapping(value = "/help", method = { RequestMethod.GET, RequestMethod.POST })
	public String help(Model model, HttpServletRequest request) {
		return "redirect:https://h5.m.taobao.com/mlapp/olist.html?spm=a2141.7756461.2.6";
//		return "searchv2/helpapp";
	}
	
	@RequestMapping(value = "/customer", method = { RequestMethod.GET, RequestMethod.POST })
	public String kefu(Model model, HttpServletRequest request) {
		return "searchv2/customer";
	}

	/**
	 * 根据位数生成验证码
	 * 
	 * @param size
	 *            位数
	 * @return
	 */
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
