package com.csie_chicago.csie_chicago;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.view.KeyEvent;

public class AudioSetting extends AbstractBasePreferencesActivity implements OnPreferenceChangeListener
{
	 RingtonePreference mAlarmPref ;
	 Preference issueAlertPreference;
	 EditTextPreference editTextSecondPreference;
	// ListPreference  listSecondPreference;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName("audiosetting");
		addPreferencesFromResource(R.xml.preference_setting);
		
	    mAlarmPref = (RingtonePreference) findPreference("ringtone");
		mAlarmPref.setOnPreferenceChangeListener(this);
		
		editTextSecondPreference = (EditTextPreference) findPreference("Timeout");
		editTextSecondPreference.setOnPreferenceChangeListener(this);
		
//		listSecondPreference=(ListPreference) findPreference("listPref");
//		listSecondPreference.setOnPreferenceChangeListener(this);

		issueAlertPreference = findPreference("issue_alert");
        SharedPreferences sharedPreferences= issueAlertPreference.getSharedPreferences();

		if (sharedPreferences.getBoolean("issue_alert", false)){
			mAlarmPref.setEnabled(true);
			editTextSecondPreference.setEnabled(true);
			//listSecondPreference.setEnabled(true);
		}
		else{
			mAlarmPref.setEnabled(false);
			editTextSecondPreference.setEnabled(false);
			//listSecondPreference.setEnabled(false);
		}
		//issueAlertPreference.setOnPreferenceChangeListener(this);
      
  

	}
	   @Override
	    protected void onResume() {
	        super.onResume();



	        // Set up a listener whenever a key changes            
	        //getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener((OnSharedPreferenceChangeListener) this);
	    }
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue)
	{
		if(preference.equals(mAlarmPref)){
//		preference.setSummary(String.valueOf(newValue));		
		mAlarmPref.setSummary(getRingtonName(Uri.parse(newValue.toString())));
        SharedPreferences sp=mAlarmPref.getPreferenceManager().getSharedPreferences();  
        sp.edit().putString("ringtone", newValue.toString()).commit();  
		}
		
		if(preference.equals(editTextSecondPreference)){
	        SharedPreferences sp=editTextSecondPreference.getPreferenceManager().getSharedPreferences();  
	        sp.edit().putString("Timeout",  String.valueOf(newValue)).commit();  
			}
		
//		if(preference.equals(listSecondPreference)){
////			preference.setSummary(String.valueOf(newValue));		
//
//	        SharedPreferences sp=listSecondPreference.getPreferenceManager().getSharedPreferences();  
//	        sp.edit().putString("listPref",  String.valueOf(newValue)).commit();  
//			}
		
        return false;
	}

	public String getRingtonName(Uri uri)
    {
        Ringtone r=RingtoneManager.getRingtone(this, uri);
        return r.getTitle(this);
    }
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,Preference preference)
	{

		if ("issue_alert".equals(preference.getKey()))
		{
			mAlarmPref.setEnabled(!mAlarmPref.isEnabled());
			editTextSecondPreference.setEnabled(!editTextSecondPreference.isEnabled());
//			listSecondPreference.setEnabled(!listSecondPreference.isEnabled());

		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
