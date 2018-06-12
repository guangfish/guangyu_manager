package com.bt.om.web.controller.api.v2;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bt.om.entity.Invitation;
import com.bt.om.entity.User;
import com.bt.om.service.IInvitationService;
import com.bt.om.service.IUserService;
import com.bt.om.util.StringUtil;
import com.bt.om.web.BasicController;
import com.bt.om.web.util.CookieHelper;

/**
 * 我的Controller
 */
@Controller
@RequestMapping(value = "/v2")
public class MyControllerV2 extends BasicController {
	@Autowired
	private IUserService userService;
	@Autowired
	private IInvitationService invitationService;

	@RequestMapping(value = "/my", method = { RequestMethod.GET, RequestMethod.POST })
	public String my(Model model, HttpServletRequest request) {
		String mobile = CookieHelper.getCookie("mobile");
		if (StringUtil.isEmpty(mobile)) {
			return "redirect:/v2/login?toUrl=/v2/my";
		} else {
			User user=userService.selectByMobile(mobile);
			model.addAttribute("user", user);
			return "searchv2/my";
		}
	}
	
	@RequestMapping(value = "/myinvitation", method = { RequestMethod.GET, RequestMethod.POST })
	public String myinvitation(Model model, HttpServletRequest request) {
		String mobile = CookieHelper.getCookie("mobile");
		if (StringUtil.isEmpty(mobile)) {
			return "redirect:/v2/login?toUrl=/v2/myinvitation";
		} else {
			User user=userService.selectByMobile(mobile);
			model.addAttribute("user", user);
			Invitation invitation=new Invitation();
			invitation.setBeInviterMobile(mobile);
			List<Invitation> invitationList = invitationService.findByMobileFriend(invitation);
			model.addAttribute("invitationList", invitationList);
			
			return "searchv2/myinvitation";
		}
	}
}
