package com.bt.om.web.controller.api.v2;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.entity.UserOrderTmp;
import com.bt.om.service.IUserOrderTmpService;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.SecurityUtil1;
import com.bt.om.util.StringUtil;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.api.v2.vo.CommonVo;
import com.bt.om.web.util.CookieHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 订单录入Controller
 */
@Controller
@RequestMapping(value = "/v2")
public class OrderControllerV2 extends BasicController {
	private static final Logger logger = Logger.getLogger(OrderControllerV2.class);
	@Autowired
	private IUserOrderTmpService userOrderTmpService;

	@RequestMapping(value = "/order", method = { RequestMethod.GET, RequestMethod.POST })
	public String orderv2(Model model, HttpServletRequest request) {
		String cookieDomain = ConfigUtil.getString("cookie.domain");
		model.addAttribute("cookieDomain", cookieDomain);
		String mobile = CookieHelper.getCookie("mobile");
		if (StringUtil.isEmpty(mobile)) {
			return "redirect:/v2/login?toUrl=/v2/order";
		} else {
			return "searchv2/order";
		}
	}

	// 订单保存
	@RequestMapping(value = "/api/ordersave", method = RequestMethod.POST)
	@ResponseBody
	public Model orderSave(Model model, HttpServletRequest request, HttpServletResponse response) {
		CommonVo commonVo = new CommonVo();
		String userId = "";
		String orderId = "";
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("userId") != null) {
				userId = obj.get("userId").getAsString();
				userId = SecurityUtil1.decrypts(userId);
			}
			if (obj.get("orderId") != null) {
				orderId = obj.get("orderId").getAsString();
			}

			// 手机号必须验证
			if (StringUtils.isEmpty(userId)) {
				commonVo.setStatus("1");
				commonVo.setDesc("请提交用户ID");
				model.addAttribute("response", commonVo);
				return model;
			}
			// 订单号必须验证
			if (StringUtils.isEmpty(orderId)) {
				commonVo.setStatus("2");
				commonVo.setDesc("请输入订单号");
				model.addAttribute("response", commonVo);
				return model;
			}
		} catch (Exception e) {
			commonVo.setStatus("3");
			commonVo.setDesc("系统繁忙，请稍后再试");
			model.addAttribute("response", commonVo);
			return model;
		}

		UserOrderTmp userOrderTmp = new UserOrderTmp();
		userOrderTmp.setOrderId(orderId);
		if (orderId.length() == 18) {
			userOrderTmp.setBelong(1);
		} else {
			userOrderTmp.setBelong(2);
		}
		userOrderTmp.setMobile(userId);
		userOrderTmp.setStatus(1);
		userOrderTmp.setCreateTime(new Date());
		userOrderTmp.setUpdateTime(new Date());
		try {
			userOrderTmpService.insert(userOrderTmp);
			commonVo.setStatus("0");
			commonVo.setDesc("订单保存成功");
		} catch (Exception e) {
			logger.info(e.getMessage());
			commonVo.setStatus("4");
			commonVo.setDesc("请勿重复提交订单号");
		}
		model.addAttribute("response", commonVo);
		return model;
	}
}
