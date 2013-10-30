package com.cpm.offlinebrowser;

import com.cpm.offlinebrowser.utils.FilesHelper;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class WebActivity extends Activity {
	private final String Path_Format = "file:///%s/offlinebrowser/sites/%i/%i/index.html";
	private String mSdCardDir = Environment.getExternalStorageDirectory().getPath();
	
	private WebView _webBrowser;
	private TextView lblTitle;
	private Button btnPre;
	private Button btnNext;
	
	private int mCurrentId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_viewer);
        
        Init();
    }

	private void Init() {
		// TODO Auto-generated method stub
		
		Intent i = getIntent();
		final int site_id = i.getIntExtra("site_id", 0);
		mCurrentId = i.getIntExtra("id", 0);
		final int total_record = i.getIntExtra("total_record", 0);
		
		String path = "file:///" + mSdCardDir + "/offlinebrowser/sites/" + site_id + "/" + String.valueOf(mCurrentId) + "/index.vb";
		Log.i("WebActivity", path);
		
		_webBrowser = (WebView)findViewById(R.id.webviewer);
		WebSettings settings = _webBrowser.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDefaultTextEncodingName("UTF-8");
		_webBrowser.loadUrl(path);
		
		lblTitle = (TextView)findViewById(R.id.lblTitle);
		lblTitle.setText(String.valueOf(mCurrentId));
		
		btnPre = (Button)findViewById(R.id.btnPre);
		btnPre.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mCurrentId--;
				if(mCurrentId < 1) mCurrentId = 1;
				lblTitle.setText(String.valueOf(mCurrentId));
				String path = mSdCardDir + "/offlinebrowser/sites/" + site_id + "/" + String.valueOf(mCurrentId) + "/index.vb";
				if(!FilesHelper.Exists(path)) return;
				
				_webBrowser.loadUrl("file:///" + path);
			}
		});
		
		btnNext = (Button)findViewById(R.id.btnNext);
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mCurrentId++;
				if(mCurrentId > total_record) mCurrentId = total_record;
				lblTitle.setText(String.valueOf(mCurrentId));
				String path = mSdCardDir + "/offlinebrowser/sites/" + site_id + "/" + String.valueOf(mCurrentId) + "/index.vb";
				if(!FilesHelper.Exists(path)) return;
				
				_webBrowser.loadUrl("file:///" + path);
			}
		});
	}

}
