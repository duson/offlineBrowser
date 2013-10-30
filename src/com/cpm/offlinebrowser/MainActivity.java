package com.cpm.offlinebrowser;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.offlinebrowser.core.Constants;
import com.cpm.offlinebrowser.dao.DownloadsDao;
import com.cpm.offlinebrowser.dao.WebsiteDao;
import com.cpm.offlinebrowser.entity.Website;
import com.cpm.offlinebrowser.service.DownloadService;
import com.cpm.offlinebrowser.sites.AbstractDownloador;
import com.cpm.offlinebrowser.sites.Other;
import com.cpm.offlinebrowser.utils.FilesHelper;
import com.cpm.offlinebrowser.utils.TypesHelper;

public class MainActivity extends Activity {
	
	private Button btnAdd;
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
	
	private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			int type = intent.getExtras().getInt("type");
			if(type == 1) {
				Toast.makeText(MainActivity.this, "网络不可用", 5000).show();
				
				ListView lst = (ListView)findViewById(R.id.lstSites);
				for(int i = 0; i < lst.getChildCount(); i++){
					View view=lst.getChildAt(i);
					Button btn = (Button)view.findViewById(R.id.btnCollect);
					if(btn != null){
						btn.setText("暂停，点击开始");
						break;
					}
				}
			}
			else if(type == 2){
				ListView lst = (ListView)findViewById(R.id.lstSites);
				for(int i = 0; i < lst.getChildCount(); i++){
					View view=lst.getChildAt(i);
					Button btn = (Button)view.findViewById(R.id.btnCollect);
					if(btn != null){
						btn.setText("完成");
						break;
					}
				}
			}
		}		
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        
        bindControl();
        BindList();
        
        registerReceiver(mDownloadReceiver, new IntentFilter("com.cpm.offlinebrowser.MainActivity"));
        bindService(new Intent(MainActivity.this, DownloadService.class), mSvrConn, BIND_AUTO_CREATE);
    }

    private void bindControl() {
		// TODO Auto-generated method stub
    
    	btnAdd = (Button)findViewById(R.id.btnAdd);
    	btnAdd.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LayoutInflater inflater = getLayoutInflater();
				final View layout = inflater.inflate(R.layout.main_site_edit, (ViewGroup)findViewById(R.id.site_edit_dialog));
				new AlertDialog.Builder(MainActivity.this).setTitle("").setView(layout)
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String name = ((TextView)layout.findViewById(R.id.txtName)).getText().toString();
						String url = ((TextView)layout.findViewById(R.id.txtUrl)).getText().toString();
						
						Website site = new Website();
						site.setName(name);
						site.setListUrl(url);						
						
						List<Website> datas = new ArrayList<Website>();
						datas.add(site);
						
						WebsiteDao siteDao = new WebsiteDao(MainActivity.this);
						siteDao.Insert(datas);
						siteDao.Close();
						
						BindList();
					}
				})
				.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				})
				.show();
			}
		});
    
    	Button btnStop = (Button)findViewById(R.id.btnStop);
    	btnStop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
				
				finish();
				//System.exit(0);
			}
		});
    }
    
    private void BindList(){
    	WebsiteDao siteDao = new WebsiteDao(MainActivity.this);
    	List<Website> data = siteDao.GetListByPage(1, 10);
    	Log.i("offlinebrowser", "data count:" + String.valueOf(data.size()));
    	ListView lst = (ListView)findViewById(R.id.lstSites);
    	lst.setAdapter(new MainListAdapter(data));
    	siteDao.Close();
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, Menu.FIRST, 0, "设置");
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
   
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST:
            	startActivity(new Intent(MainActivity.this, MyPreferenceActivity.class));
            	break;
        }
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		unbindService(mSvrConn);
		unregisterReceiver(mDownloadReceiver);
	}

    public class MainListAdapter extends BaseAdapter {

    	public final class ViewHolder{
            public TextView txtId;
            public TextView txtName;
            public TextView txtUrl;
            public TextView txtLastCollDate;
            public TextView txtCount;
            
            public Button btnView;
            public Button btnColl;
            public Button btnClear;
            public Button btnDelete;
       }

    	private List<Website> mData;
    	private ViewHolder mHolder = null;
        
        public MainListAdapter(List<Website> data){
            mData = data;
        }
        
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
    			convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.main_site_list, null);
    			mHolder.txtId = (TextView)convertView.findViewById(R.id.lblSiteId);
    			mHolder.txtName = (TextView)convertView.findViewById(R.id.lblSiteName);
    			mHolder.txtUrl = (TextView)convertView.findViewById(R.id.lblUrl);
    			mHolder.txtLastCollDate = (TextView)convertView.findViewById(R.id.lblLastCollDate);
    			mHolder.txtCount = (TextView)convertView.findViewById(R.id.lblCount);
    			mHolder.btnView = (Button)convertView.findViewById(R.id.btnView);
    			mHolder.btnColl = (Button)convertView.findViewById(R.id.btnCollect);
    			mHolder.btnClear = (Button)convertView.findViewById(R.id.btnClear);
    			mHolder.btnDelete = (Button)convertView.findViewById(R.id.btnDelete);
    			convertView.setTag(mHolder);
    		}
    		else
    			mHolder = (ViewHolder)convertView.getTag();
    		
    		mHolder.txtId.setText(String.valueOf(mData.get(position).getSiteId()));
    		mHolder.txtName.setText(mData.get(position).getName());
    		mHolder.txtUrl.setText(mData.get(position).getLlistUrl());
    		mHolder.txtLastCollDate.setText(TypesHelper.ParseDateToString(mData.get(position).getLastCollectDatetime()));
    		mHolder.txtCount.setText(mData.get(position).getUnreadCount() + "/" + mData.get(position).getTotalCount());
    		/*if(mDownloadService.isRunning())
    			mHolder.btnColl.setText("暂停，点击开始");*/
    		
    		mHolder.btnView.setOnClickListener(new OnClickListener() {
    			
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				Intent i = new Intent();
    				i.putExtra("site", mData.get(position).getSiteId());
    				i.setClass(MainActivity.this, WebListsActivity.class);
    				startActivity(i);
    			}
    		});
    		
    		final AbstractDownloador downloador = new Other(MainActivity.this);
    		mHolder.btnColl.setOnClickListener(new OnClickListener() {
    			
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				
    				final Button btnColl = (Button)v.findViewById(R.id.btnCollect);
    				
    				if(!mDownloadService.isRunning()) {
    					if(btnColl.getText().equals("暂停，点击开始")) {
    						mDownloadService.start();
    					}
    					else {
    						SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
    						String strValue = settings.getString("s_page_start", "1");
    						int startPage = TypesHelper.parseInt(strValue, 1);
    						if(startPage <= 0) startPage = 1;
    						
    						strValue = settings.getString("s_page_end", "5");
    						int endPage = TypesHelper.parseInt(strValue, 5);
    						if(endPage <= startPage ) endPage = startPage + 1;

    						mDownloadService.start(mData.get(position).getSiteId(), startPage, endPage);
    					}
    					
    					btnColl.setText("正在下载");
    				} else {
    					mDownloadService.pause();
    					btnColl.setText("暂停，点击开始");
    				}
    				
    				if(mData.get(position).getName().equals("Other1")){
    					
    					downloador.AddListener(new AbstractDownloador.DownloadListener() {
    						
    						public void progress(int finished, int total) {
    							// TODO Auto-generated method stub
    							
    							Message msg = new Message();
    							msg.what = 2;
    							msg.arg1 = finished;
    							msg.arg2 = total;
    							msg.obj = btnColl;
    							handler.sendMessage(msg);
    						}
    					});
    					
    					if(!downloador.GetRuningFlag()){
	    					new Thread(new Runnable() {
	    						public void run() {
	    							downloador.excute(mData.get(position).getSiteId());
	    						}
	    					}).start();
	    					btnColl.setText("正在下载");
    					} else {
    						downloador.SetRuningFlag(false);
    						btnColl.setText("停止，点击开始");
    					}
    				}
    			}
    		});
    		
    		mHolder.btnClear.setOnClickListener(new OnClickListener() {
    			
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    				int siteId = mData.get(position).getSiteId();
    				if(siteId == 0) return;
    				
    				if(FilesHelper.DeleteFile(Constants.Folder_SiteData + siteId)){
    					DownloadsDao downloadDao = new DownloadsDao(MainActivity.this);
    					downloadDao.ResetBySite(siteId);
    					downloadDao.Close();
    				}
    			}
    		});
    		
    		mHolder.btnDelete.setOnClickListener(new OnClickListener() {
    			
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    				int siteId = mData.get(position).getSiteId();
    				if(siteId == 0) return;
    				
    				if(FilesHelper.DeleteFile(Constants.Folder_SiteData + siteId)){
    					DownloadsDao downloadDao = new DownloadsDao(MainActivity.this);
    					downloadDao.DeleteBySiteId(siteId);
    					downloadDao.Close();
    				}
    			}
    		});
    		
    		return convertView;
    	}
    }

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2:
				Log.i("MainActivity", String.format("%s/%s", msg.arg1, msg.arg2));
				Button btnColl = (Button) msg.obj;
				btnColl.setText(String.format("%s/%s", msg.arg1,
						msg.arg2));
				if (msg.arg1 == msg.arg2) {
					btnColl.setText("完成");
				}
				break;
			}
			super.handleMessage(msg);
		}
	};
}
