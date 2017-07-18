package com.csie_chicago.csie_chicago;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class AbnormalDataProcessReceive extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("audiosetting",Activity.MODE_PRIVATE);
		context.stopService(new Intent(context, AccelDetectService.class));
		
		if(mSharedPreferences.getBoolean("issue_alert", false)){
			intent.setClass(context, AlertActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	
		context.startService(new Intent(context, AccelDetectService.class));
		
	}

}
