package com.bt.om.web.controller.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.common.SysConst;
import com.bt.om.entity.Banner;
import com.bt.om.service.IBannerService;
import com.bt.om.web.BasicController;

/**
 * banner获取Controller
 */
@Controller
public class BannerController extends BasicController {
	@Autowired
	private IBannerService bannerService;

	@RequestMapping(value = "/api/banner", method = RequestMethod.POST)
	@ResponseBody
	public Model list(Model model, HttpServletRequest request, HttpServletResponse response) {		
		List<Banner> bannerList = bannerService.selectAll();
		if(bannerList!=null && bannerList.size()>0){
			model.addAttribute(SysConst.RESULT_KEY, bannerList);
		}			
		return model;
	}
}
