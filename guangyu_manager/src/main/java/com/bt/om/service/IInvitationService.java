package com.bt.om.service;

import java.util.List;
import java.util.Map;

import com.bt.om.entity.Invitation;
import com.bt.om.vo.web.SearchDataVo;

public interface IInvitationService {
	public void insert(Invitation invitation);
	
	public List<Invitation> findByMobileFriend(Invitation invitation);
	
	public String haveInvitation(Invitation invitation);
	
	public List<Invitation> selectInvitationList(Invitation invitation);
	
	public int updateByPrimaryKeySelective(Invitation record);
	
	public List<Invitation> selectUnValidInvitationList();
	
	public int updateByPrimaryKey(Invitation record);
	
	public void selectByMobileFriend(SearchDataVo vo);
	
	public List<Invitation> selectManualInviteJiangli(Map<String,String> map);
}
