package com.cpm.offlinebrowser;

import java.util.List;

import com.cpm.offlinebrowser.WebListsActivity.WeblistAdapter;
import com.cpm.offlinebrowser.WebListsActivity.WeblistAdapter.ViewHolder;
import com.cpm.offlinebrowser.service.DownloadService;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadActivity extends Activity {
	private Button btnReturn;
	private ListView lstDownload;
	
	private DownloadService mDownloadService;
	private ServiceConnection mSvrConn = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mDownloadService = ((DownloadService.DownloadBinder)service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
	};
	
	private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String url = bundle.getString("url");
			int progress = bundle.getInt("progress");
			Log.i("DownloadActivity", url);
			
			for(int i = 0; i < lstDownload.getChildCount(); i++){
				View view=lstDownload.getChildAt(i);
				TextView lblTitle = (TextView)view.findViewById(R.id.lblTitle);
				if(lblTitle != null && lblTitle.getText().toString().equals(url)){
					ProgressBar pb = (ProgressBar)view.findViewById(R.id.progressBar);
					pb.setProgress(progress);
					break;
				}
			}
		}		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.downloads);
		
		bindControls();
		
		bindService(new Intent(DownloadActivity.this, DownloadService.class), mSvrConn, BIND_AUTO_CREATE);
		registerReceiver(mDownloadReceiver, new IntentFilter("com.cpm.offlinebrowser.WebListsActivity"));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		unbindService(mSvrConn);
		unregisterReceiver(mDownloadReceiver);
	};

	private void bindControls() {
		btnReturn = (Button)findViewById(R.id.btnReturn);
		btnReturn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(DownloadActivity.this, MainActivity.class));
			}
		});
		
		lstDownload = (ListView)findViewById(R.id.lstDownload);
		lstDownload.setAdapter(new DownloadListAdapter());
	}
	
	public class DownloadListAdapter extends BaseAdapter{
		List<String> mDownloadingList;
		private ViewHolder mHolder = null;
		
		public DownloadListAdapter(){
			mDownloadingList = mDownloadService.getDownloadingList();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDownloadingList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if(view == null){
				mHolder = new ViewHolder();
				view = LayoutInflater.from(DownloadActivity.this).inflate(R.layout.downloads_lists, null);
				mHolder.lblTitle = (TextView)view.findViewById(R.id.lblTitle);
				mHolder.pbProgress = (ProgressBar)view.findViewById(R.id.progressBar);
				view.setTag(mHolder);
			} else {
				mHolder = (ViewHolder)view.getTag();
			}
			
			mHolder.lblTitle.setText(mDownloadingList.get(position));
			
			return view;
		}
		
		public class ViewHolder{
			public TextView lblTitle;
			public ProgressBar pbProgress;
		}
	}

}
