package com.bt.om.web.controller.api.v2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.entity.AppDownload;
import com.bt.om.service.IAppDownloadService;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.api.v2.vo.AppDownloadVo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * app下载Controller
 */
@Controller
@RequestMapping(value = "/v2")
public class AppDownloadControllerV2 extends BasicController {
	@Autowired
	private IAppDownloadService appDownloadService;

	@RequestMapping(value = "/api/download", method = RequestMethod.POST)
	@ResponseBody
	public Model list(Model model, HttpServletRequest request, HttpServletResponse response) {
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
		if (appDownload != null) {
			Map<String, String> map = new HashMap<>();
			map.put("link", appDownload.getAddress());
			map.put("version", appDownload.getVersion() + "");
			map.put("ifForce", appDownload.getIfForce()+"");
			appDownloadVo.setStatus("0");
			appDownloadVo.setDesc("获取新版本成功");
			appDownloadVo.setData(map);
		} else {
			appDownloadVo.setStatus("0");
			appDownloadVo.setDesc("已经是最新版本了");
		}
		model.addAttribute("response", appDownloadVo);
		return model;
	}
}
