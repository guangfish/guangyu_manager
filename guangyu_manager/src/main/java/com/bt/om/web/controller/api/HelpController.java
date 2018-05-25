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

	// 微信浏览器淘宝使用帮助
	@RequestMapping(value = "/help.html", method = RequestMethod.GET)
	public String help(Model model, HttpServletRequest request) {
		return "search/help";
	}

	// 微信浏览器京东使用帮助
	@RequestMapping(value = "/helpjd.html", method = RequestMethod.GET)
	public String helpjd(Model model, HttpServletRequest request) {
		return "search/helpjd";
	}

	// 订单录入帮助
	@RequestMapping(value = "/helporder.html", method = RequestMethod.GET)
	public String helporder(Model model, HttpServletRequest request) {
		return "search/helporder";
	}

	// 提现帮助
	@RequestMapping(value = "/helpdraw.html", method = RequestMethod.GET)
	public String helpdraw(Model model, HttpServletRequest request) {
		return "search/helpdraw";
	}

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

}
