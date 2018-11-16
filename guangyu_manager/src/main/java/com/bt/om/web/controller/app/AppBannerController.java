package com.bt.om.web.controller.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.entity.Banner;
import com.bt.om.service.IBannerService;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.api.v2.vo.BannerVo;

/**
 * banner获取Controller
 */
@Controller
@RequestMapping(value = "/app/api/v1")
public class AppBannerController extends BasicController {
	@Autowired
	private IBannerService bannerService;

	@RequestMapping(value = "/banner", method = RequestMethod.POST)
	@ResponseBody
	public Model list(Model model, HttpServletRequest request, HttpServletResponse response) {
		BannerVo bannerVo = new BannerVo();
		bannerVo.setDesc("获取成功");
		bannerVo.setStatus("0");
		List<Banner> bannerList = bannerService.selectForApp(1);
		if (bannerList != null && bannerList.size() > 0) {
			List<Map<String, String>> list = new ArrayList<>();
			for (Banner banner : bannerList) {
				Map<String, String> map = new HashMap<>();
				map.put("imgUrl", banner.getImgUrl());
				map.put("link", banner.getLink());
				map.put("title", banner.getTitle() == null ? "" : banner.getTitle());
				map.put("width", banner.getWidth());
				map.put("height", banner.getHight());
				list.add(map);
			}
			bannerVo.setData(list);
		}
		model.addAttribute("response", bannerVo);
		return model;
	}
}
