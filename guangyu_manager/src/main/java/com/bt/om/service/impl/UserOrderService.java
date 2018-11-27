package com.bt.om.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.UserOrder;
import com.bt.om.mapper.UserOrderMapper;
import com.bt.om.service.IUserOrderService;
import com.bt.om.vo.web.SearchDataVo;

@Service
public class UserOrderService implements IUserOrderService {
	@Autowired
	private UserOrderMapper userOrderMapper;

	@Override
	public void insert(UserOrder userOrder) {
		userOrderMapper.insert(userOrder);
	}

	@Override
	public List<UserOrder> selectByMobile(String mobile) {
		return userOrderMapper.selectByMobile(mobile);
	}
	
	@Override
	public List<UserOrder> selectAllOrderByMobile(String mobile) {
		return userOrderMapper.selectAllOrderByMobile(mobile);
	}
	
	@Override
	public void updateStatus2(UserOrder userOrder) {
		userOrderMapper.updateStatus2(userOrder);
	}
	
	@Override
	public List<UserOrder> selectUnCheckOrderTaobao(UserOrder userOrder) {
		return userOrderMapper.selectUnCheckOrderTaobao(userOrder);
	}
	
	@Override
	public List<UserOrder> selectUnCheckOrderJd(UserOrder userOrder) {
		return userOrderMapper.selectUnCheckOrderJd(userOrder);
	}
	
	@Override
	public void updateByPrimaryKey(UserOrder userOrder) {
		userOrderMapper.updateByPrimaryKey(userOrder);
	}
	
	@Override
	public void updateRewardStatus(UserOrder userOrder) {
		userOrderMapper.updateRewardStatus(userOrder);
	}

	@Override
	public List<Map<String, Object>> getAllList(SearchDataVo vo) {
		Map<String, Object> searchMap = vo.getSearchMap();
		return userOrderMapper.getAllList(searchMap ,new RowBounds(vo.getStart(), vo.getSize()));
	}

	@Override
	public int getAllListCount(Map<String, Object> searchMap) {
		return userOrderMapper.getAllListCount(searchMap);
	}
	
	@Override
	public List<UserOrder> findByMobile(String mobile) {
		return userOrderMapper.findByMobile(mobile);
	}

	@Override
	public int updateOrderStatus(Integer id, Integer orderStatus, Integer putForwardStatus, Integer paymentStatus) {
		UserOrder order = userOrderMapper.selectByPrimaryKey(id);
		if (order == null) {
             return 0;			
		}
		order.setStatus1(orderStatus);
		order.setStatus2(putForwardStatus);
		order.setStatus3(paymentStatus);
		return userOrderMapper.updateByPrimaryKeySelective(order);
	}

	@Override
	public int deleteOrder(Integer id) {
		return userOrderMapper.deleteByPrimaryKey(id);
	}
	
	@Override
	public List<UserOrder> selectByInviteCode(String taInviteCode) {
		return userOrderMapper.selectByInviteCode(taInviteCode);
	}
	
	@Override
	public void selectByMobileAndOrderStatus(SearchDataVo vo){
		int count = userOrderMapper.selectByMobileAndOrderStatusCount(vo.getSearchMap());
		vo.setCount(count);
		if (count > 0) {
			vo.setList(userOrderMapper.selectByMobileAndOrderStatusList(vo.getSearchMap(), new RowBounds(vo.getStart(), vo.getSize())));
		} else {
			vo.setList(new ArrayList<UserOrder>());
		}
	}
	
	@Override
	public void getByInviteCode(SearchDataVo vo){
		int count = userOrderMapper.getByInviteCodeCount(vo.getSearchMap());
		vo.setCount(count);
		if (count > 0) {
			vo.setList(userOrderMapper.getByInviteCodeList(vo.getSearchMap(), new RowBounds(vo.getStart(), vo.getSize())));
		} else {
			vo.setList(new ArrayList<UserOrder>());
		}
	}
	
	@Override
	public List<UserOrder> selectPicUrlIsNull() {
		return userOrderMapper.selectPicUrlIsNull();
	}
	
	@Override
	public List<UserOrder> selectEstimateOrderFanli(Map<String,String> map) {
		return userOrderMapper.selectEstimateOrderFanli(map);
	}
	
	@Override
	public List<UserOrder> selectEstimateOrderJiangli(Map<String,String> map) {
		return userOrderMapper.selectEstimateOrderJiangli(map);
	}
	
	@Override
	public List<UserOrder> selectByOrderId(String orderId) {
		return userOrderMapper.selectByOrderId(orderId);
	}
	
	@Override
	public int deleteByOrderId(String orderId) {
		return userOrderMapper.deleteByOrderId(orderId);
	}
}
