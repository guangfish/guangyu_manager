package com.bt.om.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.AppDownloadLogs;
import com.bt.om.mapper.AppDownloadLogsMapper;
import com.bt.om.service.IAppDownloadLogsService;

@Service
public class AppDownloadLogsService implements IAppDownloadLogsService {
	@Autowired
	private AppDownloadLogsMapper appDownloadLogsMapper;

	@Override
	public void insert(AppDownloadLogs appDownloadLogs) {
		appDownloadLogsMapper.insert(appDownloadLogs);
	}
}
