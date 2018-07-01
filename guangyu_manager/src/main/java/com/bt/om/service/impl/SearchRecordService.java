package com.bt.om.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.SearchRecord;
import com.bt.om.mapper.SearchRecordMapper;
import com.bt.om.service.ISearchRecordService;

@Service
public class SearchRecordService implements ISearchRecordService {
	@Autowired
	private SearchRecordMapper searchRecordMapper;

	@Override
	public void insert(SearchRecord searchRecord) {
		searchRecordMapper.insert(searchRecord);
	}

	@Override
	public List<SearchRecord> selectByStatusAndTime(SearchRecord searchRecord) {
		return searchRecordMapper.selectByStatusAndTime(searchRecord);
	}
	
	@Override
	public List<SearchRecord> selectLastest(Integer minute) {
		return searchRecordMapper.selectLastest(minute);
	}
}
