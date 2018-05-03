package com.bt.om.service;

import java.util.List;
import java.util.Map;

import com.bt.om.entity.DrawCash;
import com.bt.om.vo.web.SearchDataVo;

public interface IDrawCashService {
	public void insert(DrawCash drawCash);
	
    int getDrawListCount(Map<String, Object> searchMap);
	
	List<DrawCash> getDrawList(SearchDataVo vo);
	
   int getUserOrderCountByDrawId( Integer id);
	
	List<Map<String,Object>> getUserOrderByDrawId(SearchDataVo vo,  Integer id);
	
	int deleteByPrimaryKey(Integer id);
	
	void confimPayment(Integer id) throws Exception;
}
