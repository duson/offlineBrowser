package com.cpm.offlinebrowser.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cpm.offlinebrowser.dao.DBHelper.DatabaseHelper;
import com.cpm.offlinebrowser.entity.Website;
import com.cpm.offlinebrowser.utils.TypesHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WebsiteDao {
	private DatabaseHelper		dbHelper;
	private SQLiteDatabase		db;
	public final static byte[] _writeLock = new byte[0];
	
	public WebsiteDao(Context context){
		dbHelper=new DBHelper.DatabaseHelper(context);
		db=dbHelper.getWritableDatabase();
	}
	public void Close(){
		dbHelper.close();
	}
	
	/**
	 * 判断是否已经存在
	 * @param commentId
	 * @return
	 */
	public boolean Exist(String siteName, String listUrl){
		String where="site_name=? and list_url=?";
		String[] args={siteName,listUrl};
		Cursor cursor= db.query("website", null, where, args, null, null, null);
		boolean isExist= cursor!=null && cursor.moveToNext();
		cursor.close();
		
		return isExist;
	}
	/*
	 * 得到对象
	 */
	public Website GetEntity(int siteId){
		String limit="1";
		String where="site_id=?";
		String[] args={String.valueOf(siteId)};
		List<Website> list=GetListByWhere(limit,where,args);
		if(list.size()>0){
			return list.get(0);
		}
		
		return null;
	}	
	
	/*
	 * 分页
	 */
	public List<Website> GetListByPage(int pageIndex,int pageSize){
		String limit= String.valueOf((pageIndex-1)*pageSize) + "," + String.valueOf(pageSize);
		List<Website> list=GetListByWhere(limit, null, null);
		
		return list;
	}

	/**
	 * 得到
	 * @param top
	 * @param where
	 */
	public List<Website> GetListByWhere(String limit,String where,String[] args){
		List<Website> list=new ArrayList<Website>();
		
		String orderBy="site_id desc";
		Cursor cursor=db.query("website", null, where, args, null, null, orderBy,limit);
		while(cursor!=null && cursor.moveToNext()){
			Website entity=new Website();
			entity.setSiteId(cursor.getInt(cursor.getColumnIndex("site_id")));
			entity.setName(cursor.getString(cursor.getColumnIndex("site_name")));
			entity.setListUrl(cursor.getString(cursor.getColumnIndex("list_url")));
			entity.setPage(cursor.getInt(cursor.getColumnIndex("page")));
			entity.setIsAuto(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("is_auto"))));
			entity.setCollectInterval(cursor.getInt(cursor.getColumnIndex("collect_interval")));
			entity.setLastCollectDatetime(TypesHelper.ParseDate(cursor.getString(cursor.getColumnIndex("last_collect_datetime"))));
			entity.setUnreadCount(cursor.getInt(cursor.getColumnIndex("unread_count")));
			entity.setTotalCount(cursor.getInt(cursor.getColumnIndex("total_count")));
			entity.setEnable(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("enable"))));
			entity.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
			entity.setCreateTime(TypesHelper.ParseDate(cursor.getString(cursor.getColumnIndex("createtime"))));
			
			list.add(entity);
		}
		cursor.close();
		
		return list;
	}
	
	/**
	 * 插入
	 * @param list
	 */
	public int Insert(List<Website> datas){
		List<ContentValues> list = new ArrayList<ContentValues>();
		
		for(int i=0,len=datas.size();i<len;i++){
			ContentValues contentValues = new ContentValues();
			contentValues.put("site_name",datas.get(i).getName());
			if(datas.get(i).getLlistUrl() != null)
				contentValues.put("list_url", datas.get(i).getLlistUrl());
			contentValues.put("page",datas.get(i).getPage());
			if(datas.get(i).getIsAuto() != null)
				contentValues.put("is_auto", datas.get(i).getIsAuto());
			contentValues.put("collect_interval", datas.get(i).getCollectInterval());
			if(datas.get(i).getLastCollectDatetime() != null)
				contentValues.put("last_collect_datetime",datas.get(i).getLastCollectDatetime().toString());
			contentValues.put("unread_count", datas.get(i).getUnreadCount());
			contentValues.put("total_count", datas.get(i).getTotalCount());
			if(datas.get(i).getEnable() != null)
				contentValues.put("enable", datas.get(i).getEnable());
			if(datas.get(i).getRemark() != null)
				contentValues.put("remark", datas.get(i).getRemark());
			if(datas.get(i).getCreateTime() != null)
				contentValues.put("createtime", datas.get(i).getCreateTime().toString());
			
			list.add(contentValues);
		}
		
		synchronized(_writeLock){
			db.beginTransaction();
			try{
				for(int i=0, len=list.size(); i<len; i++){
					db.insert("website", null, list.get(i));
				}
				db.setTransactionSuccessful();
				return 1;
			}catch(Exception ex){
				Log.e("website_insert", ex.getMessage());
				return 0;
			}finally{
				db.endTransaction();
			}
		}
	}
	
	/**
	 * 删除 
	 * @param favId
	 */
	public boolean Delete(int siteId) {
		String where = "site_id=?";
		String[] args = {String.valueOf(siteId)};
		try{
			db.delete("website", where, args);
			return true;
		}catch(Exception ex){
			Log.e("website_delete", ex.getMessage());
			return false;
		}
	}
	
}
