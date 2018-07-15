package com.bt.om.service;

import com.bt.om.entity.AppDownload;

public interface IAppDownloadService {
	public AppDownload selectLastest(String version);
}
