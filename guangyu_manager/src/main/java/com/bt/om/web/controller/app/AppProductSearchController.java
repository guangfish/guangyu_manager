package com.bt.om.web.controller.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.cache.JedisPool;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.app.vo.ItemVo;
import com.bt.om.web.controller.app.vo.ProductInfoVo;
import com.bt.om.web.controller.util.ProductSearchUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 商品按关键词搜索Controller
 */
@Controller
@RequestMapping(value = "/app/api")
public class AppProductSearchController extends BasicController {
	@Autowired
	private JedisPool jedisPool;

	@RequestMapping(value = "/productSearch", method = RequestMethod.POST)
	@ResponseBody
	public Model productSearch(Model model, HttpServletRequest request, HttpServletResponse response) {
		ProductInfoVo productInfoVo = null;
		String key = null;
		int pageNo = 1;
		int size = 30;
		try {
			InputStream is = request.getInputStream();
			Gson gson = new Gson();
			JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
			if (obj.get("key") != null) {
				key = obj.get("key").getAsString();
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

		if ("全部".equals(key)) {
			key = "";
		}

		Object productInfoVoObj = jedisPool.getFromCache("productSearch", key + "_" + pageNo);
		if (productInfoVoObj == null) {
			productInfoVo = ProductSearchUtil.productInfoApi(jedisPool, key, pageNo, size);
			if(productInfoVo != null){
				jedisPool.putInCache("productSearch", key + "_" + pageNo, productInfoVo, 24 * 60 * 60);
			}			
		} else {
			productInfoVo = (ProductInfoVo) productInfoVoObj;
		}

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

}
