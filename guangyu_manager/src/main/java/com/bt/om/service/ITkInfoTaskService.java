package com.bt.om.service;


import com.bt.om.entity.TkInfoTask;

public interface ITkInfoTaskService { 
    public void insertTkInfoTask(TkInfoTask tkInfoTask);
    
    public TkInfoTask selectBySign(String sign);
}
