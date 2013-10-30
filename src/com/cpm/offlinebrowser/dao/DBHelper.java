package com.cpm.offlinebrowser.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.cpm.offlinebrowser.core.Constants;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper {
	private SQLiteDatabase db;
	private DatabaseHelper dbHelper;
	public final static byte[] _writeLock = new byte[0];
	// 打开数据库
	public void OpenDB(Context context) {
		dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	// 关闭数据库
	public void Close() {
		dbHelper.close();
		if(db!=null){
			db.close();
		}
	}
	/**
	 * 插入
	 * 
	 * @param list
	 * @param table
	 *            表名
	 */
	public void Insert(List<ContentValues> list, String tableName) {
		synchronized (_writeLock) {
			db.beginTransaction();
			try {
				db.delete(tableName, null, null);
				for (int i = 0, len = list.size(); i < len; i++)
					db.insert(tableName, null, list.get(i));
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		}
	}
	public DBHelper(Context context) {
		this.dbHelper = new DatabaseHelper(context);
	}
	/**
	 * 用于初始化数据库
	 * 
	 * @author Administrator
	 * 
	 */
	public static class DatabaseHelper extends SQLiteOpenHelper {
		private static Context ctx;
		// 定义数据库文件
		private static final String DB_NAME = Constants.DB_FILE_NAME;
		// 定义数据库版本
		private static final int DB_VERSION = 1;
		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
			
			ctx = context;
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			CreateDb(db);
		}
		
		/**
		 * 创建表
		 * 
		 * @param db
		 */
		private void CreateDb(SQLiteDatabase db) {			
			AssetManager assetMgr = ctx.getResources().getAssets();
			db.beginTransaction();
			try {
				InputStream in = assetMgr.open("db_create.sql");
				
				ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
				byte[] buffer = new byte[1];
				int len = 0;
				
				while ((len = in.read(buffer)) != -1) {
					outSteam.write(buffer, 0, len);
					if(new String(buffer, "utf-8").equals(";")){
						db.execSQL(outSteam.toString());
						outSteam.reset();
					}
				}
				outSteam.close();
				in.close();
				db.setTransactionSuccessful();
				Log.e("DBHelper", "create database success");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				db.endTransaction();
			}
		}

		/**
		 * 更新版本时更新表
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			onCreate(db);
			ClearData(db);
			Log.e("User", "onUpgrade");
		}

		/**
		 * 清空数据表（仅清空无用数据）
		 * @param db
		 */
		public static void ClearData(SQLiteDatabase db){
			StringBuilder sb=new StringBuilder();
			sb.append("DELETE FROM website;");//清空表
			db.execSQL(sb.toString());
		}
		
	}
}
