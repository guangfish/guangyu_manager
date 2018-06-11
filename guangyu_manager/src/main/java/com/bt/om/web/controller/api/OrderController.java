package com.bt.om.web.controller.api;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.common.SysConst;
import com.bt.om.entity.UserOrderTmp;
import com.bt.om.enums.ResultCode;
import com.bt.om.enums.SessionKey;
import com.bt.om.service.IUserOrderTmpService;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.StringUtil;
import com.bt.om.vo.web.ResultVo;
import com.bt.om.web.BasicController;
import com.bt.om.web.util.CookieHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 订单录入Controller
 */
@Controller
public class OrderController extends BasicController {
	private static final Logger logger = Logger.getLogger(OrderController.class);
	@Autowired
	private IUserOrderTmpService userOrderTmpService;

	@RequestMapping(value = "/order.html", method = RequestMethod.GET)
	public String search(Model model, HttpServletRequest request) {
		String cookieDomain = ConfigUtil.getString("cookie.domain");
		model.addAttribute("cookieDomain", cookieDomain);
		return "search/order";
	}

	@RequestMapping(value = "/v2/order", method = { RequestMethod.GET, RequestMethod.POST })
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
		ResultVo<String> result = new ResultVo<>();
		result.setCode(ResultCode.RESULT_SUCCESS.getCode());
		result.setResultDes("");
		model = new ExtendedModelMap();
		String mobile = "";
		String orderId = "";
		String vcode = "";

		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			mobile = obj.get("mobile").getAsString();
			orderId = obj.get("orderid").getAsString();
			// 暂时屏蔽掉
			// vcode = obj.get("vcode").getAsString();

			// 手机号必须验证
			if (StringUtils.isEmpty(mobile)) {
				result.setResult("1"); // 手机号为空
				model.addAttribute(SysConst.RESULT_KEY, result);
				return model;
			}
			// 订单号必须验证
			if (StringUtils.isEmpty(orderId)) {
				result.setResult("2"); // 订单号为空
				model.addAttribute(SysConst.RESULT_KEY, result);
				return model;
			}

			// 暂时屏蔽掉
			// 验证码必须验证
			// if (StringUtils.isEmpty(vcode)) {
			// result.setResult("3"); // 验证码为空
			// model.addAttribute(SysConst.RESULT_KEY, result);
			// return model;
			// }
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(ResultCode.RESULT_FAILURE.getCode());
			result.setResultDes("系统繁忙，请稍后再试！");
			model.addAttribute(SysConst.RESULT_KEY, result);
			return model;
		}

		// 暂时屏蔽掉
		// String sessionCode =
		// request.getSession().getAttribute(SessionKey.SESSION_CODE.toString())
		// == null ? ""
		// :
		// request.getSession().getAttribute(SessionKey.SESSION_CODE.toString()).toString();
		// // 验证码有效验证
		// if (!vcode.equalsIgnoreCase(sessionCode)) {
		// result.setResult("4"); // 验证码不一致
		// model.addAttribute(SysConst.RESULT_KEY, result);
		// return model;
		// }
		// request.getSession().removeAttribute(SessionKey.SESSION_CODE.toString());

		UserOrderTmp userOrderTmp = new UserOrderTmp();
		userOrderTmp.setOrderId(orderId);
		if (orderId.length() == 18) {
			userOrderTmp.setBelong(1);
		} else {
			userOrderTmp.setBelong(2);
		}
		userOrderTmp.setMobile(mobile);
		userOrderTmp.setStatus(1);
		userOrderTmp.setCreateTime(new Date());
		userOrderTmp.setUpdateTime(new Date());
		try {
			userOrderTmpService.insert(userOrderTmp);
			result.setResult("0");// 订单保存成功
		} catch (Exception e) {
			logger.info(e.getMessage());
			result.setResult("-1");// 订单号重复提交
		}

		model.addAttribute(SysConst.RESULT_KEY, result);
		return model;
	}
}
