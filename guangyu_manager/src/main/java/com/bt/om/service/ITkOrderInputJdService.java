package com.bt.om.service;


import java.util.List;

import com.bt.om.entity.TkOrderInputJd;

public interface ITkOrderInputJdService {     
    public List<TkOrderInputJd> selectByOrderId(String orderId);
}
