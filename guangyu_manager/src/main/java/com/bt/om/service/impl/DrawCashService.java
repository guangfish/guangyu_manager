package com.bt.om.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bt.om.entity.DrawCash;
import com.bt.om.entity.UserOrder;
import com.bt.om.mapper.DrawCashMapper;
import com.bt.om.service.IDrawCashService;
import com.bt.om.vo.web.SearchDataVo;

@Service
public class DrawCashService implements IDrawCashService {
	@Autowired
	private DrawCashMapper drawCashMapper;

	@Override
	public void insert(DrawCash drawCash) {
		drawCashMapper.insert(drawCash);
	}

	@Override
	public int getDrawListCount(Map<String, Object> searchMap) {
		return drawCashMapper.getDrawListCount(searchMap);
	}

	@Override
	public List<DrawCash> getDrawList(SearchDataVo vo) {
		return drawCashMapper.getDrawList(vo.getSearchMap(), new RowBounds(vo.getStart(), vo.getSize()));
	}

	@Override
	public int getUserOrderCountByDrawId(Integer id) {
		return drawCashMapper.getUserOrderCountByDrawId(id);
	}

	@Override
	public List<Map<String,Object>> getUserOrderByDrawId(SearchDataVo vo, Integer id) {
		return drawCashMapper.getUserOrderByDrawId(id, new RowBounds(vo.getStart(), vo.getSize()));
	}

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return drawCashMapper.deleteByPrimaryKey(id);
	}

	@Override
	@Transactional
	public void confimPayment(Integer id) throws Exception {
		Date sysDate = new Date();
		DrawCash dc = drawCashMapper.selectByPrimaryKey(id);
		if(dc == null) {
			throw new Exception("drawcache dose not exist");
		}
		
		dc.setStatus(2);
		dc.setPayTime(sysDate);
		int flag = drawCashMapper.updateByPrimaryKey(dc);
		if(flag != 1) {
			throw new Exception("uppate drawcache failed");
		}
		
		flag = drawCashMapper.updateUserOrderStatus2AndStatus3(id, sysDate);
		if (flag == 0) {
			throw new Exception("update userorder failed");
		}
	}
}