package com.cpm.offlinebrowser.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cpm.offlinebrowser.dao.DBHelper.DatabaseHelper;
import com.cpm.offlinebrowser.entity.Downloads;
import com.cpm.offlinebrowser.utils.TypesHelper;

public class DownloadsDao {
	private DatabaseHelper		dbHelper;
	private SQLiteDatabase		db;
	public final static byte[] _writeLock = new byte[0];
	
	public DownloadsDao(Context context){
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
	public boolean Exist(String url){
		String where="url=?";
		String[] args={url};
		Cursor cursor= db.query("downloads", null, where, args, null, null, null);
		boolean isExist= cursor!=null && cursor.moveToNext();
		cursor.close();
		
		return isExist;
	}
	/*
	 * 得到对象
	 */
	public Downloads GetEntity(int id){
		String limit="1";
		String where="download_id=?";
		String[] args={String.valueOf(id)};
		List<Downloads> list=GetListByWhere(limit,where,args);
		if(list.size()>0){
			return list.get(0);
		}
		
		return null;
	}	
	public Downloads GetEntity(int siteId, String url){
		String limit="1";
		String where="weibsite_id=? and url=?";
		String[] args={String.valueOf(siteId), url};
		List<Downloads> list=GetListByWhere(limit,where,args);
		if(list.size()>0){
			return list.get(0);
		}
		
		return null;
	}
	
	/*
	 * 分页
	 */
	public List<Downloads> GetListByPage(int siteId, int pageIndex,int pageSize){
		String limit= String.valueOf((pageIndex-1)*pageSize) + "," + String.valueOf(pageSize);
		
		String where="weibsite_id=?";
		String[] args={String.valueOf(siteId)};

		List<Downloads> list=GetListByWhere(limit, where, args);
		
		return list;
	}

	/**
	 * 得到
	 * @param top
	 * @param where
	 */
	public List<Downloads> GetListByWhere(String limit,String where,String[] args){
		List<Downloads> list=new ArrayList<Downloads>();
		
		String orderBy="download_id desc";
		Cursor cursor=db.query("downloads", null, where, args, null, null, orderBy,limit);
		while(cursor!=null && cursor.moveToNext()){
			Downloads entity=new Downloads();
			entity.setDownloadId(cursor.getInt(cursor.getColumnIndex("download_id")));
			entity.setWeibsiteId(cursor.getInt(cursor.getColumnIndex("weibsite_id")));
			entity.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			entity.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			entity.setSavePath(cursor.getString(cursor.getColumnIndex("save_path")));
			entity.setSuccessed(cursor.getString(cursor.getColumnIndex("successed")).equals("1"));
			entity.setCleared(cursor.getString(cursor.getColumnIndex("cleared")).equals("1"));
			entity.setReaded(cursor.getString(cursor.getColumnIndex("readed")).equals("1"));
			entity.setDownloadTime(TypesHelper.ParseDate(cursor.getString(cursor.getColumnIndex("download_time"))));
			
			list.add(entity);
		}
		cursor.close();
		
		return list;
	}
	
	/**
	 * 插入
	 * @param list
	 */
	public int Insert(Downloads data){
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put("weibsite_id", data.getWeibsiteId());
			if (data.getTitle() != null)
				contentValues.put("title", data.getTitle());
			if (data.getUrl() != null)
				contentValues.put("url", data.getUrl());
			if (data.getSavePath() != null)
				contentValues.put("save_path", data.getSavePath());
			if (data.getSuccessed() != null)
				contentValues.put("successed", data.getSuccessed());
			if (data.getCleared() != null)
				contentValues.put("cleared", data.getCleared());
			if (data.getReaded() != null)
				contentValues.put("readed", data.getReaded());
			if (data.getDownloadTime() != null)
				contentValues.put("download_time", TypesHelper.ParseDateToString(data.getDownloadTime()));
			return (int) db.insert("downloads", null, contentValues);
		} catch (Exception ex) {
			Log.e("downloads_insert fail", ex.getMessage());
			return -1;
		}
	}
	
	public Boolean Update(Downloads data){
		try {
			ContentValues contentValues = new ContentValues();
			String where = "download_id=?";
			String[] args = {String.valueOf(data.getDownloadId())};

			if (data.getTitle() != null)
				contentValues.put("title", data.getTitle());
			if (data.getUrl() != null)
				contentValues.put("url", data.getUrl());
			if (data.getSavePath() != null)
				contentValues.put("save_path", data.getSavePath());
			if (data.getSuccessed() != null)
				contentValues.put("successed", data.getSuccessed());
			if (data.getCleared() != null)
				contentValues.put("cleared", data.getCleared());
			if (data.getReaded() != null)
				contentValues.put("readed", data.getReaded());
			if (data.getDownloadTime() != null)
				contentValues.put("download_time", data.getDownloadTime().toString());
			return db.update("downloads", contentValues, where, args) > 0;
		} catch (Exception ex) {
			Log.e("downloads_update fail", ex.getMessage());
			return true;
		} 		
	}
	
	public Boolean ResetById(int id){
		try {
			ContentValues contentValues = new ContentValues();
			String where = "download_id=?";
			String[] args = {String.valueOf(id)};

			contentValues.put("save_path", "");
			//contentValues.put("successed", 0);
			contentValues.put("cleared", 1);
			contentValues.put("readed", 0);
			return db.update("downloads", contentValues, where, args) > 0;
		} catch (Exception ex) {
			Log.e("downloads ResetBySite fail", ex.getMessage());
			return true;
		} 				
	}
	
	public Boolean ResetBySite(int siteId){
		try {
			ContentValues contentValues = new ContentValues();
			String where = "weibsite_id=?";
			String[] args = {String.valueOf(siteId)};

			contentValues.put("save_path", "");
			//contentValues.put("successed", 0);
			contentValues.put("cleared", 1);
			contentValues.put("readed", 0);
			return db.update("downloads", contentValues, where, args) > 0;
		} catch (Exception ex) {
			Log.e("downloads ResetBySite fail", ex.getMessage());
			return true;
		} 				
	}	
	/**
	 * 删除 
	 * @param favId
	 */
	public boolean Delete(int id) {
		String where = "download_id=?";
		String[] args = {String.valueOf(id)};
		try{
			db.delete("downloads", where, args);
			return true;
		}catch(Exception ex){
			Log.e("downloads_delete", ex.getMessage());
			return false;
		}
	}
	
	/**
	 * 删除 
	 * @param favId
	 */
	public boolean DeleteBySiteId(int siteId) {
		String where = "weibsite_id=?";
		String[] args = {String.valueOf(siteId)};
		try{
			db.delete("downloads", where, args);
			return true;
		}catch(Exception ex){
			Log.e("downloads_delete by siteId", ex.getMessage());
			return false;
		}
	}
}
