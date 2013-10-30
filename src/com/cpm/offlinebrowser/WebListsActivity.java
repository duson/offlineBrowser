package com.cpm.offlinebrowser;

import java.util.List;

import com.cpm.offlinebrowser.core.Constants;
import com.cpm.offlinebrowser.dao.DownloadsDao;
import com.cpm.offlinebrowser.entity.Downloads;
import com.cpm.offlinebrowser.service.DownloadService;
import com.cpm.offlinebrowser.sites.AbstractDownloador;
import com.cpm.offlinebrowser.sites.Other;
import com.cpm.offlinebrowser.utils.FilesHelper;
import com.cpm.offlinebrowser.utils.TypesHelper;

import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

public class WebListsActivity extends Activity {
	private Button btnRefresh;
	private Button btnReDownload;
	private ListView lstWebPage;
	private List<Downloads> mData;

	private DownloadService mDownloadService;
	
	private ServiceConnection mSvrConn = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mDownloadService = ((DownloadService.DownloadBinder)service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
	};

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.web_lists);
        
        //mDownloadReceiver = new DownloadReceiver();
        registerReceiver(mDownloadReceiver, new IntentFilter("com.cpm.offlinebrowser.WebListsActivity"));
        
        lstWebPage = (ListView)findViewById(R.id.lstWebPage);

        mData = getData();
        Log.i("WebLists", "data count:" + mData.size());
        final WeblistAdapter adapter = new WeblistAdapter();
        lstWebPage.setAdapter(adapter);
        
        btnRefresh = (Button)findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mData = getData();
				adapter.notifyDataSetChanged();
			}
		});
        
        btnReDownload = (Button)findViewById(R.id.btnReDownload);
        btnReDownload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Boolean downloadFlag = false;
				for(int i = 0; i < lstWebPage.getChildCount(); i++){
					View view=lstWebPage.getChildAt(i);
					
					CheckBox chkSelected = (CheckBox)view.findViewById(R.id.chkSelected);
					TextView lblUrl = (TextView)view.findViewById(R.id.lblUrl);
					TextView lblClear = (TextView)view.findViewById(R.id.lblClear);
					
					if(chkSelected.isChecked() && lblClear.getText() == "true"){
						mDownloadService.addList(lblUrl.getText().toString());
						downloadFlag =  true;
					}
				}

				if (downloadFlag && !mDownloadService.isRunning()) {
					mDownloadService.start(1);
					Log.i("WebListsActivity", "ReDownloadService");
				}
			}
		});
        
        bindService(new Intent(WebListsActivity.this, DownloadService.class), mSvrConn, BIND_AUTO_CREATE);
	}
	
	private List<Downloads> getData(){
		
        int siteId = getIntent().getIntExtra("site", 0);
        Log.i("WebLists", "siteId:" + String.valueOf(siteId));
        
        DownloadsDao downloadsDao = new DownloadsDao(this);
        List<Downloads> list = downloadsDao.GetListByPage(siteId, 1, 1000);
        downloadsDao.Close();
        return list;
    }
	
	public class WeblistAdapter extends BaseAdapter{

		public final class ViewHolder{
	        public TextView lblId;
	        public TextView lblUrl;
	        public TextView lblTitle;
	        public TextView lblSuccess;
	        public TextView lblClear;
	        public TextView lblCollDate;
	        public Button btnCollect;
	        public Button btnClear;
	    }
		
		private ViewHolder mHolder = null;
		
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView == null){
				mHolder = new ViewHolder();
				convertView = LayoutInflater.from(WebListsActivity.this).inflate(R.layout.web_lists_apdapter, null);
				mHolder.lblId = (TextView)convertView.findViewById(R.id.lblSiteId);
				mHolder.lblUrl = (TextView)convertView.findViewById(R.id.lblUrl);
				mHolder.lblTitle = (TextView)convertView.findViewById(R.id.lblTitle);
				mHolder.lblSuccess = (TextView)convertView.findViewById(R.id.lblSuccess);
				mHolder.lblClear = (TextView)convertView.findViewById(R.id.lblClear);
				mHolder.lblCollDate = (TextView)convertView.findViewById(R.id.lblCollDate);
				mHolder.btnCollect = (Button)convertView.findViewById(R.id.btnCollect);
				mHolder.btnClear = (Button)convertView.findViewById(R.id.btnClear);
				convertView.setTag(mHolder);
			}
			else
				mHolder = (ViewHolder)convertView.getTag();
			
			mHolder.lblId.setText(String.valueOf(mData.get(position).getDownloadId()));
			mHolder.lblUrl.setText(mData.get(position).getTitle());
			mHolder.lblTitle.setText(TypesHelper.SubstringString(mData.get(position).getTitle(), 15));
			mHolder.lblSuccess.setText(mData.get(position).getSuccessed().toString());
			mHolder.lblClear.setText(mData.get(position).getCleared().toString());
			mHolder.lblCollDate.setText(TypesHelper.ParseDateToString(mData.get(position).getDownloadTime(), "M-d HH:mm"));
			
			mHolder.btnCollect.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					mDownloadService.addList(mData.get(position).getUrl());
					if(!mDownloadService.isRunning()) {
						mDownloadService.start(mData.get(position).getWeibsiteId());
						Log.i("WebListsActivity", "start DownloadService");
					}
				}
			});
			
			mHolder.btnClear.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
			        DownloadsDao downloadsDao = new DownloadsDao(WebListsActivity.this);
			        Downloads down = downloadsDao.GetEntity(mData.get(position).getDownloadId());
    				
    				if(FilesHelper.DeleteFile(Constants.Folder_SiteData + down.getWeibsiteId() + "/" + down.getDownloadId())){
    					downloadsDao.ResetById(down.getDownloadId());
    					downloadsDao.Close();
    					mData = getData();
    					((WeblistAdapter)lstWebPage.getAdapter()).notifyDataSetChanged();
    				}
				}
			});
					
			//convertView.setClickable(true);
			convertView.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					int site_id = mData.get(position).getWeibsiteId();
					int id = mData.get(position).getDownloadId();
					
			        Intent i = new Intent();
			        i.setClass(WebListsActivity.this, WebActivity.class);
			        i.putExtra("site_id", site_id);
			        i.putExtra("id", id);
			        i.putExtra("title", mData.get(position).getTitle());
			        i.putExtra("total_record", mData.size());
			        startActivity(i);				
				}
			});
			
			return convertView;
		}
		
	}
	
	private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			int id = bundle.getInt("id");
			String progressText = bundle.getString("progressText");
			Log.i("WebListsActivity", progressText);
			
			for(int i = 0; i < lstWebPage.getChildCount(); i++){
				View view=lstWebPage.getChildAt(i);
				TextView tvId = (TextView)view.findViewById(R.id.lblSiteId);
				if(tvId != null && Integer.parseInt(tvId.getText().toString()) == id){
					TextView tvTitle = (TextView)view.findViewById(R.id.lblTitle);
					tvTitle.setText(progressText);
					break;
				}
			}
			
			int progress = bundle.getInt("progress");
			if(progress == 0){
				mData = getData();
				((WeblistAdapter)lstWebPage.getAdapter()).notifyDataSetChanged();				
			}
		}		
	};
	
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	unregisterReceiver(mDownloadReceiver);
    	unbindService(mSvrConn);
    }
}
