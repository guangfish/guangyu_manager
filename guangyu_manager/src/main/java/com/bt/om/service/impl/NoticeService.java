package com.bt.om.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.Notice;
import com.bt.om.mapper.NoticeMapper;
import com.bt.om.service.INoticeService;

@Service
public class NoticeService implements INoticeService {
	@Autowired
	private NoticeMapper noticeMapper;

	@Override
	public List<Notice> selectAll() {
		return noticeMapper.selectAll();
	}
}
