package com.bt.om.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.om.entity.AppDownload;
import com.bt.om.mapper.AppDownloadMapper;
import com.bt.om.service.IAppDownloadService;

@Service
public class AppDownloadService implements IAppDownloadService {
	@Autowired
	private AppDownloadMapper appDownloadMapper;

	@Override
	public AppDownload selectLastest(Integer version) {
		return appDownloadMapper.selectLastest(version);
	}
}
