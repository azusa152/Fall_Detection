package com.csie_chicago.csie_chicago;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Option extends Activity {
	private Button callphone_settingbtn, sms_settingbtn;
	private Button back_btn, startGPS;
	private void checkGPSOption() {
		Intent intent = new Intent(this, GPSService.class);
		LocationManager localLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (localLocationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			startService(intent);
		} else {
			stopService(intent);
		}
		return;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);

		callphone_settingbtn = (Button) findViewById(R.id.phone);
		startGPS = (Button) findViewById(R.id.gps);
		sms_settingbtn = (Button) findViewById(R.id.sms);
		back_btn = (Button) findViewById(R.id.back);

		callphone_settingbtn.setOnClickListener(callphone_setting_listener);
		sms_settingbtn.setOnClickListener(sms_setting_listener);
		startGPS.setOnClickListener(startgps_listener);
		back_btn.setOnClickListener(back_listener);
		

	}

	OnClickListener back_listener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			finish();

            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            //overridePendingTransition(R.anim.bottom_in,R.anim.bottom_out);
		}
	};

	OnClickListener callphone_setting_listener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			Intent it = new Intent(getBaseContext(), ActivityPhoneCall.class);
			startActivity(it);
           // overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
         overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
		}
	};

	OnClickListener sms_setting_listener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			Intent it = new Intent(getBaseContext(), SmsSettingActivity.class);
			startActivity(it);

            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
		}
	};

	OnClickListener startgps_listener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			Intent localIntent = new Intent(
					"android.settings.LOCATION_SOURCE_SETTINGS");
			startActivity(localIntent);

            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
		}
	};
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}