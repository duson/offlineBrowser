/**
 * 
 */
package com.cpm.offlinebrowser.sites;

import java.util.List;

import com.cpm.offlinebrowser.MainActivity;
import com.cpm.offlinebrowser.MyPreferenceActivity;
import com.cpm.offlinebrowser.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.cpm.offlinebrowser.utils.FilesHelper;
import com.cpm.offlinebrowser.utils.HtmlHelper;
import com.cpm.offlinebrowser.utils.NetHelper;
import com.cpm.offlinebrowser.utils.TypesHelper;

/**
 * @author CNIT
 * 
 */
public class Cnblogs extends AbstractDownloador {
	private static final String Folder_Cnblogs = "/sdcard/offlinebrowser/sites/cnblogs/";
	
	public Cnblogs(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}


	public void excute(int siteId) {
		// TODO Auto-generated method stub

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mCtx);
		String strValue = settings.getString("s_page_start", "1");
		int startPage = TypesHelper.parseInt(strValue, 1);
		
		strValue = settings.getString("s_page_end", "5");
		int endPage = TypesHelper.parseInt(strValue, 5);
		Log.i("myTag", "page:" + startPage + "-" + endPage);
		
		for(int p=startPage; p<=endPage; p++){
			String src = NetHelper.GetContentFromUrl("http://www.cnblogs.com/p" + p);
			int start = src.indexOf("<div id=\"post_list\">");
			int end = src.indexOf("<div id=\"pager_bottom\">"); 
			src = src.substring(start, end);
			List<String> links = HtmlHelper.getLinks(src, "<\\s*a\\s+[^>]*class=\"titlelnk\" href=\"([^\"]*)\"[^>]*>");
			
			String link;
			for (int i = 0; i < links.size(); i++) {
				link = links.get(i);
				Log.i("myTag", "link:" + link);
				saveWeb(link);
			}

			if (_listener != null)
				_listener.progress(p + 1, 2);
		}
	}

	private void saveWeb(String url) {
		String src = NetHelper.GetContentFromUrl(url);
		
		int start = src.indexOf("<title>");
		int end = src.indexOf("</title>"); 
		String title = src.substring(start + 7, end);
		Log.i("myTag", "title:" + title);
		if(title == null || title == "") 
			title = "1";
		else {
			title = title.replace(" ", "");
			title = TextUtils.htmlEncode(title);
		}

		start = src.indexOf("<div id=\"topics\">");
		if(start < 0) start = src.indexOf("<div id=\"main\">");
		if(start < 0) start = 0;
		end = src.indexOf("<div id=\"footer\">");
		if(end < 0) end = src.length();
		src = src.substring(start, end);
		Log.i("myTag", "src:" + src);

		List<String> images = HtmlHelper.getImages(src);
		String imagePath;
		for (int i = 0; i < images.size(); i++) {
			imagePath = images.get(i);
			Log.i("myTag", "img src:" + imagePath);
			NetHelper.loadImageFromUrlWithStore(Folder_Cnblogs + title + "/", imagePath, "aaa");
		}
		
		String imgName;
		String tmpPath;
		for(String path : images){
			tmpPath = path.replace("\\", "/");
			if(tmpPath.lastIndexOf(".") > 0)
				imgName = tmpPath.substring(tmpPath.lastIndexOf("/") + 1, tmpPath.lastIndexOf("."));
			else
				imgName = tmpPath.substring(tmpPath.lastIndexOf("/") + 1);
			src = HtmlHelper.replaceImagePath(src, path, imgName + "." + "aaa");
		}
				
		FilesHelper.Create(Folder_Cnblogs + title + "/index.html", src);
	}

	public Boolean SaveOnePage(int downloadId){
		return true;
	}


	@Override
	protected Integer doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		return null;
	}
}
