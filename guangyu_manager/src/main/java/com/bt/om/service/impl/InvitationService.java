package com.bt.om.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.Invitation;
import com.bt.om.mapper.InvitationMapper;
import com.bt.om.service.IInvitationService;
import com.bt.om.vo.web.SearchDataVo;

@Service
public class InvitationService implements IInvitationService {
	@Autowired
	private InvitationMapper invitationMapper;

	@Override
	public void insert(Invitation invitation) {
		invitationMapper.insert(invitation);
	}

	@Override
	public List<Invitation> findByMobileFriend(Invitation invitation) {
		return invitationMapper.findByMobileFriend(invitation);
	}
	
	@Override
	public String haveInvitation(Invitation invitation) {
		return invitationMapper.haveInvitation(invitation);
	}
	
	@Override
	public List<Invitation> selectInvitationList(Invitation invitation) {
		return invitationMapper.selectInvitationList(invitation);
	}
	
	@Override
	public int updateByPrimaryKeySelective(Invitation record){
		return invitationMapper.updateByPrimaryKeySelective(record);
	}
	
	@Override
	public List<Invitation> selectUnValidInvitationList() {
		return invitationMapper.selectUnValidInvitationList();
	}
	
	public int updateByPrimaryKey(Invitation record){
		return invitationMapper.updateByPrimaryKey(record);
	}
	
	@Override
	public void selectByMobileFriend(SearchDataVo vo){
		int count = invitationMapper.selectByMobileFriendCount(vo.getSearchMap());
		vo.setCount(count);
		if (count > 0) {
			vo.setList(invitationMapper.selectByMobileFriendList(vo.getSearchMap(), new RowBounds(vo.getStart(), vo.getSize())));
		} else {
			vo.setList(new ArrayList<Invitation>());
		}
	}
	
	@Override
	public List<Invitation> selectManualInviteJiangli(Map<String,String> map) {
		return invitationMapper.selectManualInviteJiangli(map);
	}
}
