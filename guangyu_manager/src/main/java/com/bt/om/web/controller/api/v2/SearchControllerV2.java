package com.bt.om.web.controller.api.v2;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.entity.Banner;
import com.bt.om.entity.ProductInfo;
import com.bt.om.service.IBannerService;
import com.bt.om.service.IProductInfoService;
import com.bt.om.system.GlobalVariable;
import com.bt.om.vo.web.SearchDataVo;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.vo.JsonResult;
import com.bt.om.web.util.SearchUtil;

/**
 * 逛鱼搜索Controller
 */
@Controller
public class SearchControllerV2 extends BasicController {
	@Autowired
	IProductInfoService productInfoService;
	
	@Autowired
	private IBannerService bannerService;
	
	@RequestMapping(value = "/searchv2", method = { RequestMethod.GET, RequestMethod.POST })
	public String search(Model model, HttpServletRequest request) {
		String ua=request.getHeader("User-Agent");
		String ifWeixinBrower="no";
		if((ua.toLowerCase()).contains("micromessenger")){
			ifWeixinBrower="yes";
		}
		model.addAttribute("ifWeixinBrower", ifWeixinBrower);
		
		List<Banner> bannerList = bannerService.selectAll();
		model.addAttribute("bannerList", bannerList);
		
		float rate=Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate"));
		model.addAttribute("rate", rate);
		List<ProductInfo> productInfoList=productInfoService.selectProductInfoListRand(30);
		for(ProductInfo productInfo:productInfoList){
			double commission=productInfo.getCommission();
			if(commission<=1){
				productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1")));
			}else if(commission>1 && commission<=5){
				productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1-5")));
			}else if(commission>5 && commission<=10){
				productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.5-10")));
			}else if(commission>10 && commission<=50){
				productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.10-50")));
			}else if(commission>50 && commission<=100){
				productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.50-100")));
			}else if(commission>100 && commission<=500){
				productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.100-500")));
			}else{
				productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.500")));
			}
		}
		model.addAttribute("productInfoList", productInfoList);
		return "searchv2/search";
	}
	
	@RequestMapping(value = "/searchmore", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchMore(Model model, HttpServletRequest request) {
		return "searchv2/more";
	}

	@ResponseBody
	@RequestMapping(value = "/api/more", method = { RequestMethod.GET, RequestMethod.POST })
	public JsonResult apiMore(Model model, HttpServletRequest request) {
		JsonResult result = new JsonResult();
		String ua=request.getHeader("User-Agent");
		String ifWeixinBrower="no";
		if((ua.toLowerCase()).contains("micromessenger")){
			ifWeixinBrower="yes";
		}
		model.addAttribute("ifWeixinBrower", ifWeixinBrower);
		float rate=Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate"));
		SearchDataVo vo = SearchUtil.getVoForList();
		productInfoService.selectProductInfoList(vo);
		model.addAttribute("rate", rate);
		@SuppressWarnings("unchecked")
		List<ProductInfo> productInfoList=(List<ProductInfo>) vo.getList();
		for(ProductInfo productInfo:productInfoList){
			double commission=productInfo.getCommission();
			productInfo.setActualCommission(((float) (Math.round(
					commission * Float.parseFloat(GlobalVariable.resourceMap.get("commission.rate")) * 100))
					/ 100));
			if(commission<=1){
				productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1")));
			}else if(commission>1 && commission<=5){
				productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.1-5")));
			}else if(commission>5 && commission<=10){
				productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.5-10")));
			}else if(commission>10 && commission<=50){
				productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.10-50")));
			}else if(commission>50 && commission<=100){
				productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.50-100")));
			}else if(commission>100 && commission<=500){
				productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.100-500")));
			}else{
				productInfo.setFanli(Float.parseFloat(GlobalVariable.resourceMap.get("fanli.multiple.500")));
			}
		}
		
		result.setList(productInfoList);
		result.setCurPage(vo.getStart());
		result.setMaxPage(vo.getCount()/vo.getSize());
		result.setTolrow(vo.getCount());
		return result;
	}
}
