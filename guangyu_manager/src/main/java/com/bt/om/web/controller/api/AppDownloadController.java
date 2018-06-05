package com.bt.om.web.controller.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.common.SysConst;
import com.bt.om.entity.AppDownload;
import com.bt.om.service.IAppDownloadService;
import com.bt.om.web.BasicController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * app下载Controller
 */
@Controller
public class AppDownloadController extends BasicController {
	@Autowired
	private IAppDownloadService appDownloadService;

	@RequestMapping(value = "/api/download", method = RequestMethod.POST)
	@ResponseBody
	public Model list(Model model, HttpServletRequest request, HttpServletResponse response) {
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
			model.addAttribute(SysConst.RESULT_KEY, appDownload);
		}
		return model;
	}
}
