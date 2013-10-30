package com.cpm.offlinebrowser.sites;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cpm.offlinebrowser.MainActivity;
import com.cpm.offlinebrowser.MyPreferenceActivity;
import com.cpm.offlinebrowser.core.Constants;
import com.cpm.offlinebrowser.dao.DownloadsDao;
import com.cpm.offlinebrowser.entity.Downloads;
import com.cpm.offlinebrowser.service.DownloadService;
import com.cpm.offlinebrowser.utils.FilesHelper;
import com.cpm.offlinebrowser.utils.HtmlHelper;
import com.cpm.offlinebrowser.utils.NetHelper;
import com.cpm.offlinebrowser.utils.TypesHelper;

public class Other extends AbstractDownloador {
	private DownloadsDao mDao;
	
	public Other(Context ctx) {	
		super(ctx);
		mDao = new DownloadsDao(ctx);
	}

	public void excute(int siteId) {
		// TODO Auto-generated method stub
		if(mRuning) return;
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mCtx);
		String strValue = settings.getString("s_page_start", "1");
		int startPage = TypesHelper.parseInt(strValue, 1);
		if(startPage <= 0) startPage = 1;
		
		strValue = settings.getString("s_page_end", "5");
		int endPage = TypesHelper.parseInt(strValue, 5);
		if(endPage <= startPage ) endPage = startPage + 1;
			
		Log.i("Other", "page:" + startPage + "-" + endPage);
		if (_listener != null)
			_listener.progress(0, endPage);
		
		int try_count = 0;
		mRuning = true;
		for(int p=startPage; p<=endPage; p++){
			if(!mRuning) break;
			String src = NetHelper.GetContentFromUrl("http://www.mitao95.com/p/list_3_" + p + ".html");
			
			int start = src.indexOf("<div class=list>");
			int end = src.indexOf("<div class=page>"); 
			if(start == -1) start = 0;
			if(end == -1) end = src.length();
			
			src = src.substring(start, end);
			List<String> links = HtmlHelper.getLinks(src, "<\\s*a\\s+[^>]* href=\"([^\"]*)\"[^>]*>");
			
			String link;
			for (int i = 0; i < links.size(); i++) {
				if(!mRuning) break;
				link = "http://www.mitao95.com" + links.get(i);
				Downloads down =mDao.GetEntity(siteId, link);
				if(down != null) continue;
				
				Log.i("Other", "link:" + link);
				if(!saveWeb(link, siteId) && try_count < 3){
					i--;
					try_count++;
				}
				else
					try_count = 0;
				Log.i("Other", "save web " + link + ", try count:" + try_count);
			}

			if (_listener != null)
				_listener.progress(p, endPage);
		}
		
		mDao.Close();
		mRuning = false;
	}

	private Boolean saveWeb(String url, int siteId) {
		Boolean result = false;
				
		Downloads down = null;
		down = mDao.GetEntity(siteId, url);
		if(down == null){
			down = new Downloads();		
			down.setWeibsiteId(siteId);
			down.setUrl(url);
			down.setTitle(url);
		
			int newId = mDao.Insert(down);
			Log.i("Other", "newId:" + String.valueOf(newId));
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
			Log.i("Other", "src:" + src);
	
			List<String> images = HtmlHelper.getImages(src);
			String imagePath;
			int down_img_success_count = 0;
			for (int i = 0; i < images.size(); i++) {
				imagePath = images.get(i);
				Log.i("Other", "img src:" + imagePath);
				if(NetHelper.loadImageFromUrlWithStore(subDir, imagePath, "aaa") != null)
					down_img_success_count++;
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
					
			FilesHelper.Create(subDir + "index.html", src);
			down.setSavePath(subDir + "index.html");
			
			result = true;
		} catch(Exception ex){
			Log.e("Other", "saveWeb error:" + ex.getMessage());
			FilesHelper.DeleteFile(subDir);
		}
		
		down.setSuccessed(result);
		down.setDownloadTime(TypesHelper.ParseDate(new Date().toString()));
		Boolean updateResult = mDao.Update(down);
		Log.i("Other", "update old downoload result:" + updateResult.toString());
		
		return result;
	}

	public Boolean SaveOnePage(int downloadId)
	{
		Downloads down = mDao.GetEntity(downloadId);
		if(down == null) return false;

		FilesHelper.DeleteFile(Constants.Folder_SiteData + down.getWeibsiteId() + "/" + downloadId);
		return saveWeb(down.getUrl(), down.getWeibsiteId());
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(Integer... params) {
		if(mRuning) return 0;
		
		int siteId = params[0];
		
		int startPage = 1;
		int endPage = 5;

		if(params.length > 2){
			startPage = params[1];
			endPage = params[2];
		}
		else{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mCtx);
			
			String strValue = settings.getString("s_page_start", "1");
			startPage = TypesHelper.parseInt(strValue, 1);
			if(startPage <= 0) startPage = 1;
			
			strValue = settings.getString("s_page_end", "5");
			endPage = TypesHelper.parseInt(strValue, 5);
			if(endPage <= startPage ) endPage = startPage + 1;			
		}
			
		Log.i("Other", "page:" + startPage + "-" + endPage);
		if (_listener != null)
			_listener.progress(0, endPage);
		publishProgress(0, 100);
		
		int try_count = 0;
		mRuning = true;
		for(int p=startPage; p<=endPage; p++){
			if(!mRuning) break;
			String src = NetHelper.GetContentFromUrl("http://www.mitao95.com/p/list_3_" + p + ".html");
			
			int start = src.indexOf("<div class=list>");
			int end = src.indexOf("<div class=page>"); 
			if(start == -1) start = 0;
			if(end == -1) end = src.length();
			
			src = src.substring(start, end);
			List<String> links = HtmlHelper.getLinks(src, "<\\s*a\\s+[^>]* href=\"([^\"]*)\"[^>]*>");
			
			String link;
			for (int i = 0; i < links.size(); i++) {
				if(!mRuning) break;
				link = "http://www.mitao95.com" + links.get(i);
				Downloads down =mDao.GetEntity(siteId, link);
				if(down != null) continue;
				
				Log.i("Other", "link:" + link);
				if(!saveWeb(link, siteId) && try_count < 3){
					i--;
					try_count++;
				}
				else
					try_count = 0;
				Log.i("Other", "save web " + link + ", try count:" + try_count);
			}

			if (_listener != null)
				_listener.progress(p, endPage);
		}
		
		mDao.Close();
		mRuning = false;
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}
	
}
