/**
 * 
 */
package com.cpm.offlinebrowser.sites;

import android.content.Context;
import android.os.AsyncTask;

/**
 * @author CNIT
 *
 */
public abstract class AbstractDownloador extends AsyncTask<Integer, Integer, Integer> {
	
	protected Context mCtx;
	protected Boolean mRuning = false;
	
	protected DownloadListener _listener;
	
	public AbstractDownloador(Context ctx){
		mCtx = ctx;
	}
	
	public abstract void excute(int siteId);
	
	public abstract Boolean SaveOnePage(int downloadId);
	
	public void SetRuningFlag(Boolean val){
		mRuning = val;
	}
	public Boolean GetRuningFlag(){
		return mRuning;
	}
	
	public void AddListener(DownloadListener listener){
		_listener = listener;
	}
	
	public interface DownloadListener{
		void progress(int finished, int total);
	}
}

