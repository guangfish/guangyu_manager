package com.bt.om.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.List;

import org.junit.Test;

import com.bt.om.entity.UserOrder;
import com.bt.om.mapper.UserOrderMapper;
import com.bt.om.taobao.api.ProductApi;
import com.bt.om.taobao.api.product.ProductInfoVo;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.StringUtil;

@ContextConfiguration(locations = { "classpath*:/applicationContext-test.xml" })
public class BaseTest extends AbstractJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserOrderMapper userOrderMapper;

	@Test
	public void selectUserOrder() {
		List<UserOrder> userOrderList = userOrderMapper.selectPicUrlIsNull();
		System.out.println(userOrderList.size());
		String retStr = "";
		ProductInfoVo productInfoVo = null;
		for (UserOrder userOrder : userOrderList) {
			retStr = ProductApi.getProductInfo(userOrder.getProductId());
			if (StringUtil.isNotEmpty(retStr)) {
				productInfoVo = GsonUtil.GsonToBean(retStr, ProductInfoVo.class);
				try{
					userOrder.setProductImgUrl(productInfoVo.getTbk_item_info_get_response().getResults().getN_tbk_item().get(0).getPict_url()+"_200x200.jpg");
					userOrderMapper.updateByPrimaryKey(userOrder);
				}catch(Exception e){
					e.printStackTrace();
				}
			}

			retStr = "";
			productInfoVo = null;
		}
	}
}
