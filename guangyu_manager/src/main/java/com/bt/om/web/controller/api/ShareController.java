package com.bt.om.web.controller.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bt.om.web.BasicController;

/**
 * 逛鱼分享Controller
 */
@Controller
public class ShareController extends BasicController {

	@RequestMapping(value = "/api/share.html", method = RequestMethod.GET)
	public String share(Model model, HttpServletRequest request) {
		return "search/share";
	}
}
