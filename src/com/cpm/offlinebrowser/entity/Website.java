package com.cpm.offlinebrowser.entity;

import java.util.Date;

public class Website {
	private int _siteId;
	private String _name;
	private String _listUrl;
	private int _page;
	private Boolean _isAuto;
	private int _collectInterval;
	private Date _lastCollectDatetime;
	private int _unreadCount;
	private int _totalCount;
	private Boolean _enable;
	private String _remark;
	private Date _createTime;
	
	public int getSiteId() {
		return _siteId;
	}
	public void setSiteId(int _siteId) {
		this._siteId = _siteId;
	}
	public String getName() {
		return _name;
	}
	public void setName(String _name) {
		this._name = _name;
	}
	public String getLlistUrl() {
		return _listUrl;
	}
	public void setListUrl(String _listUrl) {
		this._listUrl = _listUrl;
	}
	public int getPage() {
		return _page;
	}
	public void setPage(int _page) {
		this._page = _page;
	}
	public Boolean getIsAuto() {
		return _isAuto;
	}
	public void setIsAuto(Boolean _isAuto) {
		this._isAuto = _isAuto;
	}
	public int getCollectInterval() {
		return _collectInterval;
	}
	public void setCollectInterval(int _collectInterval) {
		this._collectInterval = _collectInterval;
	}
	public Date getLastCollectDatetime() {
		return _lastCollectDatetime;
	}
	public void setLastCollectDatetime(Date _lastCollectDatetime) {
		this._lastCollectDatetime = _lastCollectDatetime;
	}
	public int getUnreadCount() {
		return _unreadCount;
	}
	public void setUnreadCount(int _unreadCount) {
		this._unreadCount = _unreadCount;
	}
	public int getTotalCount() {
		return _totalCount;
	}
	public void setTotalCount(int _totalCount) {
		this._totalCount = _totalCount;
	}
	public Boolean getEnable() {
		return _enable;
	}
	public void setEnable(Boolean _enable) {
		this._enable = _enable;
	}
	public String getRemark() {
		return _remark;
	}
	public void setRemark(String _remark) {
		this._remark = _remark;
	}
	public Date getCreateTime() {
		return _createTime;
	}
	public void setCreateTime(Date _createTime) {
		this._createTime = _createTime;
	}
	
}
