package com.cpm.offlinebrowser;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

@TargetApi(11)
public class MyPreferenceActivity extends PreferenceActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        addPreferencesFromResource(R.xml.preferences);
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        
        EditTextPreference p = (EditTextPreference)findPreference("s_page_start");
        p.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
        	public boolean onPreferenceChange(Preference p, Object newValue) {
        		// TODO Auto-generated method stub
        		Log.e("myTag", "key:" + p.getKey() + " value:" + newValue.toString());
        		
        		int value = Integer.valueOf(newValue.toString());
				if (value <= 0) {
					Toast.makeText(MyPreferenceActivity.this, "format invalid", 2000)
							.show();
					return false;
				}
        		
        		return true;
        	}
        });
    }
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub
		
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	
	
	public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

}
