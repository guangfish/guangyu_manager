package com.bt.om.web.controller.api.v2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.adtime.common.lang.DateUtil;
import com.bt.om.entity.Invitation;
import com.bt.om.entity.UserOrder;
import com.bt.om.service.IInvitationService;
import com.bt.om.service.IUserOrderService;
import com.bt.om.util.SecurityUtil1;
import com.bt.om.util.StringUtil;
import com.bt.om.web.controller.api.v2.vo.UserOrderVo;
import com.bt.om.web.BasicController;
import com.bt.om.web.util.CookieHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 逛鱼订单搜索Controller
 */
@Controller
@RequestMapping(value = "/v2")
public class SearchOrderControllerV2 extends BasicController {
	@Autowired
	private IUserOrderService userOrderService;
	@Autowired
	private IInvitationService invitationService;

	@RequestMapping(value = "/searchorder", method = RequestMethod.GET)
	public String searchorderv2(Model model, HttpServletRequest request) {
		String mobile = CookieHelper.getCookie("mobile");
		if (StringUtil.isEmpty(mobile)) {
			return "redirect:/v2/login?toUrl=/v2/searchorder";
		} else {
			int friendNum = 0;
			int friendNumValid = 0;
			int friendNumNoValid = 0;
			int rewardAll = 0;
			int reward = 0;
			double totalCommission = 0;
			float tCommission = 0;
			double totalUCommission = 0;
			float tUCommission = 0;
			int canDrawOrderNum = 0;
			int uncanDrawOrderNum = 0;
			Invitation invitationVo = new Invitation();
			invitationVo.setInviterMobile(mobile);
			List<Invitation> invitationList = invitationService.selectInvitationList(invitationVo);

			if (invitationList != null && invitationList.size() > 0) {
				for (Invitation invitation : invitationList) {
					// 邀请已激活获得奖励
					if (invitation.getStatus() == 2 && invitation.getReward() == 1) {
						friendNumValid = friendNumValid + 1;
						reward = reward + invitation.getMoney();
					}
					// 邀请未激活，预计可获得奖励
					if (invitation.getStatus() == 1) {
						friendNumNoValid = friendNumNoValid + 1;
						rewardAll = rewardAll + invitation.getMoney();
					}
				}
				friendNum = invitationList.size();
			}

			List<UserOrder> userOrderList = userOrderService.selectAllOrderByMobile(mobile);
			List<UserOrder> userOrderCanDrawList = new ArrayList<>();
			List<UserOrder> userOrderNotCanDrawList = new ArrayList<>();
			for (UserOrder userOrder : userOrderList) {
				if ("订单结算".equals(userOrder.getOrderStatus())) {
					canDrawOrderNum = canDrawOrderNum + 1;
					totalCommission = totalCommission + userOrder.getCommission3() * userOrder.getFanliMultiple();
					userOrderCanDrawList.add(userOrder);
				} else {
					uncanDrawOrderNum = uncanDrawOrderNum + 1;
					totalUCommission = totalUCommission + userOrder.getCommission3() * userOrder.getFanliMultiple();
					userOrderNotCanDrawList.add(userOrder);
				}
			}

			tCommission = ((float) (Math.round(totalCommission * 100)) / 100);
			tUCommission = ((float) (Math.round(totalUCommission * 100)) / 100);

			model.addAttribute("friendNum", friendNum);
			model.addAttribute("friendNumValid", friendNumValid);
			model.addAttribute("friendNumNoValid", friendNumNoValid);
			model.addAttribute("rewardAll", rewardAll);
			model.addAttribute("reward", reward);
			model.addAttribute("tCommission", tCommission);
			model.addAttribute("tUCommission", tUCommission);
			model.addAttribute("canDrawOrderNum", canDrawOrderNum);
			model.addAttribute("uncanDrawOrderNum", uncanDrawOrderNum);

			model.addAttribute("userOrderCanDrawList", userOrderCanDrawList);
			model.addAttribute("userOrderNotCanDrawList", userOrderNotCanDrawList);
			return "searchv2/searchorder";
		}
	}

	// 查询订单列表
	@RequestMapping(value = "/api/searchorder", method = RequestMethod.POST)
	@ResponseBody
	public UserOrderVo searchOrder(Model model, HttpServletRequest request, HttpServletResponse response) {
		UserOrderVo userOrderVo = new UserOrderVo();
		String userId = "";
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("userId") != null) {
				userId = obj.get("userId").getAsString();
				userId = SecurityUtil1.decrypts(userId);
			}
		} catch (IOException e) {
			userOrderVo.setStatus("1");
			userOrderVo.setDesc("系统繁忙，请稍后再试");
			return userOrderVo;
		}

		// 手机号码必须验证
		if (StringUtils.isEmpty(userId)) {
			userOrderVo.setStatus("2");
			userOrderVo.setDesc("请提交手机号码");
			return userOrderVo;
		}

		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		// 判断是否可以提现 0：不可提现 1：可提现
		String canDraw = "0";
		int friendNum = 0;
		int friendNumValid = 0;
		int friendNumNoValid = 0;
		int rewardAll = 0;
		int reward = 0;

		double totalCommission = 0;
		int canDrawOrderNum = 0;
		int uncanDrawOrderNum = 0;
		try {
			List<UserOrder> userOrderList = userOrderService.selectAllOrderByMobile(userId);
			Invitation invitationVo = new Invitation();
			invitationVo.setInviterMobile(userId);
			List<Invitation> invitationList = invitationService.selectInvitationList(invitationVo);

			if (invitationList != null && invitationList.size() > 0) {
				for (Invitation invitation : invitationList) {
					// 邀请已激活获得奖励
					if (invitation.getStatus() == 2 && invitation.getReward() == 1) {
						friendNumValid = friendNumValid + 1;
						reward = reward + invitation.getMoney();
					}
					// 邀请未激活，预计可获得奖励
					if (invitation.getStatus() == 1) {
						friendNumNoValid = friendNumNoValid + 1;
						rewardAll = rewardAll + invitation.getMoney();
					}
				}
				friendNum = invitationList.size();
			}

			if (userOrderList != null && userOrderList.size() > 0) {
				for (UserOrder userOrder : userOrderList) {
					if ("订单结算".equals(userOrder.getOrderStatus()) || "已结算".equals(userOrder.getOrderStatus())) {
						canDrawOrderNum = canDrawOrderNum + 1;
						totalCommission = totalCommission + userOrder.getCommission3() * userOrder.getFanliMultiple();
					} else {
						uncanDrawOrderNum = uncanDrawOrderNum + 1;
					}
				}
				if (totalCommission > 0) {
					canDraw = "1";
				}
			}

			if (userOrderList != null && userOrderList.size() > 0) {
				for (UserOrder userOrder : userOrderList) {
					HashMap<String, String> map = new HashMap<>();
					map.put("imgUrl", userOrder.getProductImgUrl()==null?"":userOrder.getProductImgUrl());
					map.put("productName", userOrder.getProductInfo());
					map.put("commission", ((!"订单结算".equals(userOrder.getOrderStatus()))
							? ("预估￥" + userOrder.getCommission3()) : "￥" + (userOrder.getCommission3())));

					map.put("multiple", "" + userOrder.getFanliMultiple());
					map.put("orderStatus", userOrder.getOrderStatus());
					map.put("orderTime", DateUtil.formatDate(userOrder.getCreateTime(), DateUtil.CHINESE_PATTERN));
					list.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		userOrderVo.setCanDraw(canDraw);
		userOrderVo.setCanDrawOrderNum(canDrawOrderNum);
		userOrderVo.setUncanDrawOrderNum(uncanDrawOrderNum);
		userOrderVo.setFriendNum(friendNum);
		userOrderVo.setFriendNumNoValid(friendNumNoValid);
		userOrderVo.setFriendNumValid(friendNumValid);
		userOrderVo.setReward(reward);
		userOrderVo.setRewardAll(rewardAll);
		userOrderVo.setTotalCommission((float) (Math.round(totalCommission * 100)) / 100);
		userOrderVo.setMap(list);
		return userOrderVo;
	}
}
