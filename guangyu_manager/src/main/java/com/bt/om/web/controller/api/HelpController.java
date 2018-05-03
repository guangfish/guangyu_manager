package com.bt.om.web.controller.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bt.om.web.BasicController;

/**
 * 逛鱼帮助Controller
 */
@Controller
public class HelpController extends BasicController {

	// 苹果手机使用淘宝帮助
	@RequestMapping(value = "/helptbios.html", method = RequestMethod.GET)
	public String helpTbIos(Model model, HttpServletRequest request) {
		return "search/helptbios";
	}

	// 安卓手机使用淘宝帮助
	@RequestMapping(value = "/helptbandroid.html", method = RequestMethod.GET)
	public String helpTbAndroid(Model model, HttpServletRequest request) {
		return "search/helptbandroid";
	}

	// 苹果手机使用京东帮助
	@RequestMapping(value = "/helpjdios.html", method = RequestMethod.GET)
	public String helpJdIos(Model model, HttpServletRequest request) {
		return "search/helpjdios";
	}

	// 安卓手机使用京东帮助
	@RequestMapping(value = "/helpjdandroid.html", method = RequestMethod.GET)
	public String helpJdAndroid(Model model, HttpServletRequest request) {
		return "search/helpjdandroid";
	}

	// 提现帮助
	@RequestMapping(value = "/helpdraw.html", method = RequestMethod.GET)
	public String helpDraw(Model model, HttpServletRequest request) {
		return "search/helpdraw";
	}
}
