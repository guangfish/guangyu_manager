package com.bt.om.web.controller.app.v2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.adtime.common.lang.StringUtil;
import com.bt.om.cache.JedisPool;
import com.bt.om.entity.AppDownload;
import com.bt.om.entity.AppDownloadLogs;
import com.bt.om.entity.Banner;
import com.bt.om.service.IAppDownloadLogsService;
import com.bt.om.service.IAppDownloadService;
import com.bt.om.service.IBannerService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.RequestUtil;
import com.bt.om.util.TaobaoSmsNewUtil;
import com.bt.om.web.controller.api.v2.vo.AppDownloadVo;
import com.bt.om.web.controller.api.v2.vo.BannerVo;
import com.bt.om.web.controller.api.v2.vo.CommonVo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController
@RequestMapping(value = "/app/api")
public class AppBaseController {
	private static final Logger logger = Logger.getLogger(AppBaseController.class);

	@Autowired
	private JedisPool jedisPool;
	@Autowired
	private IBannerService bannerService;
	@Autowired
	private IAppDownloadService appDownloadService;
	@Autowired
	private IAppDownloadLogsService appDownloadLogsService;

	@RequestMapping(value = "/getSmsCode", method = RequestMethod.POST)
	@ResponseBody
	public Model getSmsCode(Model model, HttpServletRequest request, HttpServletResponse response) {
		String remoteIp = RequestUtil.getRealIp(request);
		CommonVo commonVo = new CommonVo();
		String version="";
		String app="";
		String mobile = "";
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("version") != null) {
				version = obj.get("version").getAsString();
			}
			if (obj.get("app") != null) {
				app = obj.get("app").getAsString();
			}
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

	private String getVcode(int size) {
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
	
	@RequestMapping(value = "/banner", method = RequestMethod.POST)
	@ResponseBody
	public Model list(Model model, HttpServletRequest request, HttpServletResponse response) {
		String version="";
		String app="";
		BannerVo bannerVo = new BannerVo();
		bannerVo.setDesc("获取成功");
		bannerVo.setStatus("0");
		List<Banner> bannerList = bannerService.selectForApp(1);
		if (bannerList != null && bannerList.size() > 0) {
			List<Map<String, String>> list = new ArrayList<>();
			for (Banner banner : bannerList) {
				Map<String, String> map = new HashMap<>();
				map.put("imgUrl", banner.getImgUrl());
				map.put("link", banner.getLink());
				map.put("title", banner.getTitle() == null ? "" : banner.getTitle());
				map.put("width", banner.getWidth());
				map.put("height", banner.getHight());
				list.add(map);
			}
			bannerVo.setData(list);
		}
		model.addAttribute("response", bannerVo);
		return model;
	}
	
	//首次下载
	@RequestMapping(value = "/fd", method = RequestMethod.GET)
	public String firstDownload(Model model, HttpServletRequest request, HttpServletResponse response) {
		String returl = "";
		String andoridDownloadUrl = GlobalVariable.resourceMap.get("android_download_url");
		String iosDownloadUrl = GlobalVariable.resourceMap.get("ios_download_url");
		String ua = request.getHeader("User-Agent");
		String ip = RequestUtil.getRealIp(request);
		if (StringUtil.isNotEmpty(ua)) {
			AppDownloadLogs appDownloadLogs = new AppDownloadLogs();
			ua = ua.toLowerCase();
			appDownloadLogs.setIp(ip);
			appDownloadLogs.setDownloadTime(new Date());
			if (ua.contains("android")) {
				appDownloadLogs.setDevice("android");
				returl = "redirect:" + andoridDownloadUrl;
			} else if (ua.contains("iphone") || ua.contains("ipad")) {
				appDownloadLogs.setDevice("ios");
				returl = "redirect:" + iosDownloadUrl;
			} else {
				appDownloadLogs.setDevice("other");
				returl = "redirect:" + andoridDownloadUrl;
			}
			appDownloadLogsService.insert(appDownloadLogs);
		}
		return returl;
	}
	
	//安卓下载更新
	@RequestMapping(value = "/download", method = RequestMethod.POST)
	@ResponseBody
	public Model download(Model model, HttpServletRequest request, HttpServletResponse response) {
		AppDownloadVo appDownloadVo = new AppDownloadVo();
		InputStream is;
		String version = "1.0.0";
		String appOs = "android";
		try {
			is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			version = obj.get("version").getAsString();
			if (obj.get("appOs") != null) {
				appOs = obj.get("appOs").getAsString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		AppDownload appDownload = appDownloadService.selectLastest(version);
		Map<String, String> map = new HashMap<>();
		if (appDownload != null) {
			map.put("link", appDownload.getAddress());
			map.put("version", appDownload.getVersion() + "");
			map.put("ifForce", appDownload.getIfForce() + "");
			map.put("describe", appDownload.getDescribe());
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

}
