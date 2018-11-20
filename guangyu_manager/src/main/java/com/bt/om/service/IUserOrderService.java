package com.bt.om.service;


import java.util.List;
import java.util.Map;

import com.bt.om.entity.UserOrder;
import com.bt.om.vo.web.SearchDataVo;

public interface IUserOrderService {   
    public void insert(UserOrder userOrder);
    public List<UserOrder> selectByMobile(String mobile);
    public List<UserOrder> selectAllOrderByMobile(String mobile);
    public void updateStatus2(UserOrder userOrder);
    public List<UserOrder> selectUnCheckOrderTaobao(UserOrder userOrder);
    public List<UserOrder> selectUnCheckOrderJd(UserOrder userOrder);
    public void updateByPrimaryKey(UserOrder userOrder);   
    public void updateRewardStatus(UserOrder userOrder);  
    
    int getAllListCount(Map<String, Object> searchMap);
    List<Map<String, Object>> getAllList(SearchDataVo vo);
    public List<UserOrder> findByMobile(String mobile);
    
    public int updateOrderStatus(Integer id, Integer orderStatus, Integer putForwardStatus, Integer paymentStatus);
    
    int deleteOrder(Integer id);
    
    public List<UserOrder> selectByInviteCode(String taInviteCode);
    
    public void selectByMobileAndOrderStatus(SearchDataVo vo);
    
    public void getByInviteCode(SearchDataVo vo);
    
    public List<UserOrder> selectPicUrlIsNull();
    
    public List<UserOrder> selectEstimateOrderFanli(Map<String,String> map);
    
    public List<UserOrder> selectEstimateOrderJiangli(Map<String,String> map);
}
