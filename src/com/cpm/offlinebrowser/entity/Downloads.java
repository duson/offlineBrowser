package com.cpm.offlinebrowser.entity;

import java.util.Date;

public class Downloads {
	private int _downloadId;
	private int _weibsiteId;
	private String _title;
	private String _url;
	private String _savePath;
	private Boolean _successed;
	private Boolean _cleared;
	private Boolean _readed;
	private Date _downloadTime;
	
	public int getDownloadId() {
		return _downloadId;
	}
	public void setDownloadId(int _downloadId) {
		this._downloadId = _downloadId;
	}
	public int getWeibsiteId() {
		return _weibsiteId;
	}
	public void setWeibsiteId(int _weibsiteId) {
		this._weibsiteId = _weibsiteId;
	}
	public String getTitle() {
		return _title;
	}
	public void setTitle(String _title) {
		this._title = _title;
	}
	public String getUrl() {
		return _url;
	}
	public void setUrl(String _url) {
		this._url = _url;
	}
	public String getSavePath() {
		return _savePath;
	}
	public void setSavePath(String _savePath) {
		this._savePath = _savePath;
	}
	public Boolean getSuccessed() {
		return _successed;
	}
	public void setSuccessed(Boolean _successed) {
		this._successed = _successed;
	}
	public Boolean getCleared() {
		return _cleared;
	}
	public void setCleared(Boolean _cleared) {
		this._cleared = _cleared;
	}
	public Boolean getReaded() {
		return _readed;
	}
	public void setReaded(Boolean _readed) {
		this._readed = _readed;
	}
	public Date getDownloadTime() {
		return _downloadTime;
	}
	public void setDownloadTime(Date _downloadTime) {
		this._downloadTime = _downloadTime;
	}

}
