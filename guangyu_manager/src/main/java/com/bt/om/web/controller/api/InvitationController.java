package com.bt.om.web.controller.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.common.SysConst;
import com.bt.om.entity.Invitation;
import com.bt.om.enums.ResultCode;
import com.bt.om.service.IInvitationService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.util.ConfigUtil;
import com.bt.om.vo.web.ResultVo;
import com.bt.om.web.BasicController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 我要邀请Controller
 */
@Controller
public class InvitationController extends BasicController {
	@Autowired
	private IInvitationService invitationService;

	@RequestMapping(value = "/api/invitation.html", method = RequestMethod.GET)
	public String invitation(Model model, HttpServletRequest request) {
		int reward=Integer.parseInt(GlobalVariable.resourceMap.get("reward.money"));
		model.addAttribute("reward", reward);
		return "search/invitation";
	}

	// 保存邀请信息
	@RequestMapping(value = "/api/saveinvitation", method = RequestMethod.POST)
	@ResponseBody
	public Model saveinvItation(Model model, HttpServletRequest request, HttpServletResponse response) {
		ResultVo<String> result = new ResultVo<>();
		result.setCode(ResultCode.RESULT_SUCCESS.getCode());
		result.setResultDes("");
		model = new ExtendedModelMap();
		String mobileMe = "";
		String mobileFriend = "";

		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			mobileMe = obj.get("mobileme").getAsString();
			mobileFriend = obj.get("mobilefriend").getAsString();

			// 我的手机号码必须验证
			if (StringUtils.isEmpty(mobileMe)) {
				result.setResult("1");
				model.addAttribute(SysConst.RESULT_KEY, result);
				return model;
			}
			// 朋友的手机号码必须验证
			if (StringUtils.isEmpty(mobileFriend)) {
				result.setResult("2");
				model.addAttribute(SysConst.RESULT_KEY, result);
				return model;
			}
		} catch (IOException e) {
			result.setCode(ResultCode.RESULT_FAILURE.getCode());
			result.setResultDes("系统繁忙，请稍后再试！");
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}

		Invitation invitation = new Invitation();
		invitation.setInviterMobile(mobileMe);
		invitation.setBeInviterMobile(mobileFriend);
		invitation.setStatus(1);
		invitation.setReward(1);
		invitation.setMoney(Integer.parseInt(GlobalVariable.resourceMap.get("reward.money")));		
		invitation.setCreateTime(new Date());
		invitation.setUpdateTime(new Date());
		
		String mobile=invitationService.haveInvitation(invitation);
		if (StringUtils.isEmpty(mobile)) {
			invitationService.insert(invitation);
		}else{
			//邀请的用户已经在使用逛鱼搜索或已被邀请或已邀请过别人
			result.setResult("3");
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}

		result.setResult("0");// 保存成功
		model.addAttribute(SysConst.RESULT_KEY, result);
		return model;
	}
}
