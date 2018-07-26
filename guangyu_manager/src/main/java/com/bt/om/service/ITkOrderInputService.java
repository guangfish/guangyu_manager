package com.bt.om.service;


import java.util.List;
import java.util.Map;

import com.bt.om.entity.TkOrderInput;

public interface ITkOrderInputService {     
    public List<TkOrderInput> selectByOrderId(String orderId);
    
    public List<TkOrderInput> selectByMap(Map<String,Object> map);
    
    public void insert(TkOrderInput tkOrderInput);
    
    public void updateByOrderId(TkOrderInput record);
    
    public void truncateTkOrderInput();
}
