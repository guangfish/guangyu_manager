package com.bt.om.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.List;

import org.junit.Test;

import com.bt.om.entity.User;
import com.bt.om.entity.UserOrder;
import com.bt.om.mapper.UserMapper;
import com.bt.om.mapper.UserOrderMapper;
import com.bt.om.taobao.api.ProductApi;
import com.bt.om.taobao.api.product.ProductInfoVo;
import com.bt.om.util.GsonUtil;
import com.bt.om.util.StringUtil;

@ContextConfiguration(locations = { "classpath*:/applicationContext-test.xml" })
public class TaobaoIdPidMatchTest extends AbstractJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserOrderMapper userOrderMapper;

	@Test
	public void selectUserOrder() {
		List<User> userList = userMapper.selectAll();
		String pid = "176864894";
		for (User user : userList) {
			List<UserOrder> userOrderList = userOrderMapper.selectTaoBaoOrderByMobile(user.getMobile());
			if (userOrderList != null && userOrderList.size() > 0) {
				UserOrder userOrder = userOrderList.get(0);
				String orderId = userOrder.getOrderId();
				String taobaoId = orderId.substring(16, 18) + orderId.substring(14, 16);
				user.setPid(pid);
				user.setTaobaoId(taobaoId);
				userMapper.updateByPrimaryKey(user);
			}
		}
	}
}
