package com.bt.om.web.controller.xcx;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bt.om.entity.SmsPushStats;
import com.bt.om.mapper.SmsPushStatsMapper;
import com.bt.om.util.RequestUtil;

@RestController
@RequestMapping(value = "/xcx/api")
public class SmsPushController {
//	private static final Logger logger = Logger.getLogger(AksPushController.class);
	@Autowired
	private SmsPushStatsMapper smsPushStatsMapper;

	@RequestMapping(value = "/aks/stats", method = RequestMethod.GET)
	public String aksStats(Model model, HttpServletRequest request, HttpServletResponse response) {
		String ip = RequestUtil.getRealIp(request);
		String redirectUrl = "https://ies.acadsoc.com.cn/Ips/shortMeal/selectCourse.htm?source=XXZJBT_2051225";
		SmsPushStats smsPushStats=new SmsPushStats();
		smsPushStats.setCreateTime(new Date());
		smsPushStats.setIp(ip);
		smsPushStats.setType("aks");
		smsPushStatsMapper.insert(smsPushStats);
		
		return "redirect:" + redirectUrl;
	}
	
	@RequestMapping(value = "/jy/stats", method = RequestMethod.GET)
	public String jyStats(Model model, HttpServletRequest request, HttpServletResponse response) {
		String ip = RequestUtil.getRealIp(request);
		String redirectUrl = "https://www.jingyuxiaoban.com/market/180419/index.html?source_mobile=17700000148&staff_no=0yuanst";
		SmsPushStats smsPushStats=new SmsPushStats();
		smsPushStats.setCreateTime(new Date());
		smsPushStats.setIp(ip);
		smsPushStats.setType("jy");
		smsPushStatsMapper.insert(smsPushStats);
		
		return "redirect:" + redirectUrl;
	}
}
