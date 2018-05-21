package com.bt.om.service;


import java.util.List;

import com.bt.om.entity.SearchRecord;

public interface ISearchRecordService {         
    public void insert(SearchRecord searchRecord);
    
    public List<SearchRecord> selectByStatusAndTime(SearchRecord searchRecord);
}
