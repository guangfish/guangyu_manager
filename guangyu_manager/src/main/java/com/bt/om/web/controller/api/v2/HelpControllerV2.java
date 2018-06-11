package com.bt.om.web.controller.api.v2;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bt.om.web.BasicController;

/**
 * 帮助Controller
 */
@Controller
@RequestMapping(value = "/v2")
public class HelpControllerV2 extends BasicController {

	@RequestMapping(value = "/help", method = { RequestMethod.GET, RequestMethod.POST })
	public String help(Model model, HttpServletRequest request) {
		return "searchv2/help";
	}
}
