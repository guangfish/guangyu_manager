package com.bt.om.service;


import java.util.List;

import com.bt.om.entity.TkOrderInput;

public interface ITkOrderInputService {     
    public List<TkOrderInput> selectByOrderId(String orderId);
    
    public void insert(TkOrderInput tkOrderInput);
    
    public void updateByOrderId(TkOrderInput record);
}
