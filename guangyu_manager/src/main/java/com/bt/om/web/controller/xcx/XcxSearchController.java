package com.bt.om.web.controller.xcx;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bt.om.cache.JedisPool;
import com.bt.om.entity.User;
import com.bt.om.service.IUserService;
import com.bt.om.taobao.api.TaoKouling;
import com.bt.om.taobao.api.TklResponse;
import com.bt.om.util.ConfigUtil;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.SecurityUtil1;
import com.bt.om.util.StringUtil;
import com.bt.om.web.controller.xcx.util.ItemVo;
import com.bt.om.web.controller.xcx.util.ProductInfoVo;
import com.bt.om.web.controller.xcx.util.XcxProductSearchUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController
@RequestMapping(value = "/xcx/api")
public class XcxSearchController {
	private static final Logger logger = Logger.getLogger(XcxSearchController.class);
	@Autowired
	private JedisPool jedisPool;
	@Autowired
	private IUserService userService;

	@RequestMapping(value = "/productList", method = RequestMethod.POST)
	@ResponseBody
	public Model productList(Model model, HttpServletRequest request, HttpServletResponse response) {
		ProductInfoVo productInfoVo = null;
		String userId="";
		String mobile="";
		String key = null;
		int ifHot = 1;
		int isSearch=1;
		int pageNo = 1;
		int size = 30;
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("userId") != null) {
				userId = obj.get("userId").getAsString();
				mobile = SecurityUtil1.decrypts(userId);
				
			}
			if (obj.get("key") != null) {
				key = obj.get("key").getAsString();
			}
			if (obj.get("ifHot") != null) {
				ifHot = obj.get("ifHot").getAsInt();
			}
			if (obj.get("isSearch") != null) {
				isSearch = obj.get("isSearch").getAsInt();
			}
			if (obj.get("pageNo") != null) {
				pageNo = obj.get("pageNo").getAsInt();
			}
			if (obj.get("size") != null) {
				size = obj.get("size").getAsInt();
			}
		} catch (IOException e) {
			productInfoVo = new ProductInfoVo();
			productInfoVo.setStatus("1");
			productInfoVo.setDesc("系统繁忙，请稍后再试");
			productInfoVo.setData(new ItemVo());
			model.addAttribute("response", productInfoVo);
			return model;
		}
		
		String pid = "";
		if (StringUtil.isNotEmpty(mobile)) {
			User user = userService.selectByMobile(mobile);
			if (user != null) {
				if (StringUtil.isNotEmpty(user.getPid())) {
					pid = user.getPid();
				}
			}
		}		
		if(StringUtil.isEmpty(pid)){
			pid=ConfigUtil.getString("alimama.abigpush.default.pid", "176864894");
		}

		if ("全部".equals(key)) {
			key = "";
		}

		String sort = "total_sales";
		if (ifHot == 2) {
			sort = "tk_total_sales";
		}

		productInfoVo = XcxProductSearchUtil.productInfoApi(key,isSearch,userId,pid, pageNo, size, sort);

		if (productInfoVo == null) {
			productInfoVo = new ProductInfoVo();
			productInfoVo.setDesc("未查到商品信息");
			productInfoVo.setStatus("2");
			productInfoVo.setData(new ItemVo());
			model.addAttribute("response", productInfoVo);
			return model;
		}

		model.addAttribute("response", productInfoVo);

		return model;
	}

	@RequestMapping(value = "/productItemInfo", method = RequestMethod.POST)
	@ResponseBody
	public Model productItemInfo(Model model, HttpServletRequest request, HttpServletResponse response) {
		ProductInfoVo productInfoVo = null;
		String userId = "";
		String mobile = "";
		String categoryName = "";
		String productId = "";
		String tkUrl = "";
		String title = "";
		String imgUrl = "";
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("userId") != null) {
				userId = obj.get("userId").getAsString();
				mobile = SecurityUtil1.decrypts(userId);
			}
			if (obj.get("categoryName") != null) {
				categoryName = obj.get("categoryName").getAsString();
			}
			if (obj.get("productId") != null) {
				productId = obj.get("productId").getAsString();
			}
			if (obj.get("tkUrl") != null) {
				tkUrl = obj.get("tkUrl").getAsString();
			}
			if (obj.get("title") != null) {
				title = obj.get("title").getAsString();
			}
			if (obj.get("imgUrl") != null) {
				imgUrl = obj.get("imgUrl").getAsString();
			}
		} catch (IOException e) {
			productInfoVo = new ProductInfoVo();
			productInfoVo.setStatus("1");
			productInfoVo.setDesc("系统繁忙，请稍后再试");
			productInfoVo.setData(new ItemVo());
			model.addAttribute("response", productInfoVo);
			return model;
		}

		Object redisTklObj = jedisPool.getFromCache("tkl", productId);
		String tkl = "";
		if (redisTklObj != null) {
			logger.info(productId + "淘口令缓存命中");
			tkl = (String) redisTklObj;
		} else {
			String tklStr = TaoKouling.createTkl(tkUrl, title, imgUrl);
			if (StringUtil.isNotEmpty(tklStr)) {
				TklResponse tklResponse = GsonUtil.GsonToBean(tklStr, TklResponse.class);
				tkl = tklResponse.getTbk_tpwd_create_response().getData().getModel();
				jedisPool.putInCache("tkl", productId, tklResponse.getTbk_tpwd_create_response().getData().getModel(),
						7 * 24 * 60 * 60);
			}
		}
		
		String pid = "";
		if (StringUtil.isNotEmpty(mobile)) {
			User user = userService.selectByMobile(mobile);
			if (user != null) {
				if (StringUtil.isNotEmpty(user.getPid())) {
					pid = user.getPid();
				}
			}
		}		
		if(StringUtil.isEmpty(pid)){
			pid=ConfigUtil.getString("alimama.abigpush.default.pid", "176864894");
		}

		productInfoVo = XcxProductSearchUtil.productInfoApi(categoryName,1,userId,pid, 1, 10, "tk_total_sales");
		if (productInfoVo == null) {
			productInfoVo = new ProductInfoVo();
			productInfoVo.setDesc("未查到商品信息");
			productInfoVo.setStatus("2");
			productInfoVo.setData(new ItemVo());
			model.addAttribute("response", productInfoVo);
			return model;
		}

		productInfoVo.getData().setTkl(tkl);
		model.addAttribute("response", productInfoVo);

		return model;
	}

}
