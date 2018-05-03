package com.bt.om.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.Invitation;
import com.bt.om.mapper.InvitationMapper;
import com.bt.om.service.IInvitationService;

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
}
