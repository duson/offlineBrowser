package com.cpm.offlinebrowser.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cpm.offlinebrowser.DownloadActivity;
import com.cpm.offlinebrowser.R;
import com.cpm.offlinebrowser.core.Constants;
import com.cpm.offlinebrowser.dao.DownloadsDao;
import com.cpm.offlinebrowser.entity.Downloads;
import com.cpm.offlinebrowser.utils.FilesHelper;
import com.cpm.offlinebrowser.utils.HtmlHelper;
import com.cpm.offlinebrowser.utils.NetHelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ViewDebug.FlagToString;
import android.widget.RemoteViews;
import android.widget.Toast;

public class DownloadService extends Service {

	private static final String ACTIOIN_NOTIFY = "OfficeLineBrowswer_Action_Notify"; 
	private static final int NOTIFY_DOWNLOAD = 1001; 
	
	private NotificationManager mNotifyMgr;
	private Notification mNotify;
	private DownloadTask mDownloadTask;
	
	protected Boolean mRunningFlag = false;
	private DownloadsDao mDao;
	
	private List<String> mDownloadSet = new ArrayList<String>();
	
	private DownloadBinder mBinder = new DownloadBinder();
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		mNotifyMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		mNotify = new Notification();
		mNotify.contentView = new RemoteViews(getPackageName(), R.layout.service_download);
		mNotify.icon = R.drawable.ic_launcher;
		
		Intent i = new Intent(DownloadService.this, DownloadActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		mNotify.contentIntent = PendingIntent.getBroadcast(this, 0, i, 0);
				
		//mNotify.contentView.setOnClickPendingIntent(R.id.progressBar, 
		//		PendingIntent.getActivity(DownloadService.this, 0, new Intent(DownloadService.this, DownloadActivity.class), FLAG_UPDATE_CURRENT));
		
		mDao = new DownloadsDao(this);
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		
		int siteId = intent.getIntExtra("siteId", 1);
		int startPage = intent.getIntExtra("startPage", 1);
		int endPage = intent.getIntExtra("endPage", 2);
		
		if(mDownloadTask == null || mDownloadTask.getStatus() != AsyncTask.Status.RUNNING){
			mDownloadTask = new DownloadTask();
			mDownloadTask.execute(siteId, startPage, endPage);
		}
		//Log.i("DownloadService", "onStart:" + siteId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		mNotifyMgr.cancelAll();
		mDownloadTask.cancel(true);
		mRunningFlag = false;
	}

	public void start(int siteId, int startPage, int endPage){
		if(!mRunningFlag) mRunningFlag = true;
		
		mNotifyMgr.notify(NOTIFY_DOWNLOAD, mNotify);
		
		if(mDownloadTask == null || mDownloadTask.getStatus() != AsyncTask.Status.RUNNING){
			mDownloadTask = new DownloadTask();
			mDownloadTask.execute(siteId, startPage, endPage);
		}
	}
	public void start(int siteId){
		if(!mRunningFlag) mRunningFlag = true;
		
		mNotifyMgr.notify(NOTIFY_DOWNLOAD, mNotify);
		mNotify.tickerText = "继续";
		
		if(mDownloadTask == null || mDownloadTask.getStatus() != AsyncTask.Status.RUNNING){
			mDownloadTask = new DownloadTask();
			mDownloadTask.execute(siteId);
		}
	}
	public void start(){
		mNotify.tickerText = "继续";
		mRunningFlag = true;
		mNotifyMgr.notify(NOTIFY_DOWNLOAD, mNotify);
	}
	public void pause(){
		mNotify.tickerText = "暂停";
		mNotify.contentView.setTextViewText(R.id.txtTitle, "暂停");
		mRunningFlag = false;
		mNotifyMgr.notify(NOTIFY_DOWNLOAD, mNotify);
	}
	public Boolean isRunning(){
		if(mDownloadTask == null) return false;
		return mDownloadTask.getStatus() == AsyncTask.Status.RUNNING;
	}
	
	public void addList(String link){
		mDownloadSet.add(link);
	}	
	public List<String> getDownloadingList(){
		return mDownloadSet;
	}
	
	public class DownloadTask extends AsyncTask<Integer, Integer, Integer>{
		
		String currentText = "";
		int mDoneNum = 0;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			
			if(!NetHelper.networkIsAvailable(DownloadService.this)) {
				this.cancel(true);
				Toast.makeText(DownloadService.this, "网络不可用", 5000).show();
			}

			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Integer... params) {	
			if(params.length >=3) {
				mNotify.tickerText = "正在分析链接";
				analyseLink(params[1], params[2]);
			}
			
			int siteId = params[0];	
			int try_count = 0;
			mRunningFlag = true;
			for(int i = 0; i < mDownloadSet.size(); i++){
				while(!mRunningFlag) { 
					try {
						Thread.sleep(2000);
						mNotify.tickerText = "暂停";
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				};
				
				mNotify.tickerText = "正在下载";
				currentText = "(" + (i+1) + "/" + mDownloadSet.size() + ")链接：" + mDownloadSet.get(i);
				publishProgress(i*100/mDownloadSet.size());

				if(!saveWeb(i + 1, mDownloadSet.size(), mDownloadSet.get(i), siteId) && try_count < 3){
					i--;
					try_count++;
				}
				else {
					try_count = 0;
					mDoneNum++;
					mDownloadSet.remove(i);
				}
			}
			
			mDao.Close();
			return 1;
		}
		
		private void analyseLink(int startPage, int endPage) {
			
			if(!NetHelper.networkIsAvailable(DownloadService.this)) {
				Toast.makeText(DownloadService.this, "网络不可用", 5000).show();
				return;
			}

			for(int p = startPage; p<= endPage; p++){
				String src = NetHelper.GetContentFromUrl("http://www.mitao95.com/p/list_3_" + p + ".html");
				
				int start = src.indexOf("<div class=list>");
				int end = src.indexOf("<div class=page>"); 
				if(start == -1) start = 0;
				if(end == -1) end = src.length();
				
				src = src.substring(start, end);
				List<String> links = HtmlHelper.getLinks(src, "<\\s*a\\s+[^>]* href=\"([^\"]*)\"[^>]*>");
				
				String link;
				for (int i = 0; i < links.size(); i++) {
					link = "http://www.mitao95.com" + links.get(i);
					if(mDao.Exist(link)) continue;
					
					if(!mDownloadSet.contains(link))
						mDownloadSet.add(link);					
				}
				
				currentText = "页码：(" + p + "/" + endPage + ")";
				publishProgress(p*100/endPage);
			}
			
			currentText = "共有链接：" + mDownloadSet.size();
			publishProgress(0);
		}
		
		private Boolean saveWeb(int linkIndex, int total, String url, int siteId) {
			Boolean result = false;
					
			Downloads down = null;
			down = mDao.GetEntity(siteId, url);
			if(down == null){
				down = new Downloads();		
				down.setWeibsiteId(siteId);
				down.setUrl(url);
				down.setTitle(url);
				down.setDownloadTime(new Date());
				
				int newId = mDao.Insert(down);
				//Log.i("Other", "newId:" + String.valueOf(newId));
				if(newId <= 0) return false;
				
				down = mDao.GetEntity(newId);
			}
			
			String subDir = Constants.Folder_SiteData + siteId + "/" + String.valueOf(down.getDownloadId()) + "/";
			
			try {
							
				String src = NetHelper.GetContentFromUrl(url);
				if(src == null || src == "") return false;
				
				int start = src.indexOf("<div class=content>");
				if(start < 0) start = 0;
				int end = src.indexOf("<div class=\"pagea\">");
				if(end < 0) end = src.length();
				src = src.substring(start, end);
				//Log.i("Other", "src:" + src);
		
				List<String> images = HtmlHelper.getImages(src);
				String imagePath;
				int down_img_success_count = 0;
				Intent intent = new Intent();
				intent.setAction("com.cpm.offlinebrowser.WebListsActivity");
				Bundle bundle = new Bundle();
				for (int i = 0; i < images.size(); i++) {
					if(!mRunningFlag) return false;
					if(!NetHelper.networkIsAvailable(DownloadService.this)) {
						mRunningFlag = false;
						Toast.makeText(DownloadService.this, "网络不可用", 5000).show();
						Intent mainIntent = new Intent();
						mainIntent.setAction("com.cpm.offlinebrowser.MainActivity");
						mainIntent.putExtra("type", 1);
						sendBroadcast(mainIntent);
						return false;
					}
					
					currentText = "链接(" + linkIndex + "/" + total + "), 图片(" + (i + 1) + "/" + images.size() + "）：" + url;
					publishProgress(0);
					
					imagePath = images.get(i);
					//Log.i("Other", "img src:" + imagePath);
					if(NetHelper.loadImageFromUrlWithStore(subDir, imagePath, "aaa") != null)
						down_img_success_count++;
					
					bundle.putInt("id", down.getDownloadId());
					bundle.putInt("progress", i*100/images.size());
					bundle.putString("url", down.getUrl());
					bundle.putString("progressText", "图片（" + (i + 1) + "/" + images.size());
					intent.putExtras(bundle);
					sendBroadcast(intent);
				}
				if(down_img_success_count == 0) return false;
				
				String imgName;
				String tmpPath;
				for(String path : images){
					tmpPath = path.replace("\\", "/");
					if(tmpPath.lastIndexOf(".") > 0)
						imgName = tmpPath.substring(tmpPath.lastIndexOf("/") + 1, tmpPath.lastIndexOf("."));
					else
						imgName = tmpPath.substring(tmpPath.lastIndexOf("/") + 1);
					src = src.replace(path, imgName + "." + "aaa");
				}
						
				FilesHelper.Create(subDir + "index.vb", src);
				down.setSavePath(subDir + "index.vb");
				
				result = true;
			} catch(Exception ex){
				Log.e("Other", "saveWeb error:" + ex.getMessage());
			}
			finally{
				if(result) {
					down.setSuccessed(result);
					down.setCleared(!result);
					mDao.Update(down);
				}
				else {
					mDao.Delete(down.getDownloadId());
					FilesHelper.DeleteFile(subDir);
				}
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			mNotify.contentView.setTextViewText(R.id.txtTitle, "完成" + mDoneNum + "篇");
			publishProgress(100);
			
			Intent mainIntent = new Intent();
			mainIntent.setAction("com.cpm.offlinebrowser.MainActivity");
			mainIntent.putExtra("type", 2);
			sendBroadcast(mainIntent);

			mNotifyMgr.cancelAll();
			DownloadService.this.stopSelf();
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			
			int progress = values[0];
			if(progress > 0)
				mNotify.contentView.setProgressBar(R.id.progressBar, 100, progress, false);
			mNotifyMgr.notify(NOTIFY_DOWNLOAD, mNotify);
			if(!currentText.equals("")){
				mNotify.contentView.setTextViewText(R.id.txtTitle, currentText);
			}
		}
	}

	public class DownloadBinder extends Binder{
		public DownloadService getService() {
            return DownloadService.this; 
       } 
	}
}
