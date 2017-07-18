package com.csie_chicago.csie_chicago;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class AlertActivity extends Activity implements OnClickListener {
	private static final String TAG = AlertActivity.class.getSimpleName();
	private AudioManager audio;
	private Button mCancelButton;
	private Ringtone mRingTone;
	private SharedPreferences mSharedPreferences, callphoneSharedPreferences,
			smsSharedPreferences, gpsSharedPreferences;
	private AudioManager ringerVolume;
	private Timer timer;
	private Vibrator vibrate;
	private String tt;

	private void tt() {
		tt = smsSharedPreferences.getString("smstext", "")
				+ "http://maps.google.com/maps?q="
				+ gpsSharedPreferences.getString("gpslatitude", "0") + ","
				+ gpsSharedPreferences.getString("gpslongitude", "0");
	}

	private void setRingtone() {
		// String str1 = getString(R.string.ringtonePref);
		// String str1 = "ringtone";
		// String str2 = this.mSharedPreferences.getString(str1, "Default");
		// if (str2.equalsIgnoreCase("default"))
		// ;
		// for (Uri localUri = Settings.System.DEFAULT_RINGTONE_URI;; localUri =
		// Uri.parse(str2)) {
		// Log.d(TAG, "RingUri <" + localUri + ">");
		// this.mRingTone = RingtoneManager.getRingtone(this, localUri);
		// return;
		// }

		String str1 = "ringtone";
		String str2 = this.mSharedPreferences.getString(str1, "Default");
		this.mRingTone = RingtoneManager.getRingtone(this, Uri.parse(str2));
	}

	private void startFlash() {
		View localView = findViewById(R.id.background);
		Animation localAnimation = AnimationUtils.loadAnimation(this,
				R.anim.flash);
		localAnimation.setRepeatCount(-1);
		localAnimation.setDuration(2000L);
		localAnimation.setRepeatMode(2);
		localView.startAnimation(localAnimation);
	}

	private void startVibrate() {
		long[] arrayOfLong = { 500L, 300L };
		vibrate.vibrate(arrayOfLong, 0);
	}

	public void onClick(View paramView) {
			finish();
		}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.alert);

		// this.mSharedPreferences = PreferenceManager
		// .getDefaultSharedPreferences(this);
		this.mSharedPreferences = getSharedPreferences("audiosetting",
				Activity.MODE_PRIVATE);
		this.callphoneSharedPreferences = getSharedPreferences(
				"callphonesetting", Activity.MODE_PRIVATE);
		this.smsSharedPreferences = getSharedPreferences("smssetting",
				Activity.MODE_PRIVATE);
		this.gpsSharedPreferences = getSharedPreferences("GPSsetting",
				Activity.MODE_PRIVATE);

		this.mCancelButton = ((Button) findViewById(R.id.cancelButton));
		this.mCancelButton.setOnClickListener(this);
		this.vibrate = ((Vibrator) getApplication().getSystemService(
				Service.VIBRATOR_SERVICE));
		this.audio = ((AudioManager) getSystemService(Service.AUDIO_SERVICE));
		this.ringerVolume = ((AudioManager) getSystemService(Service.AUDIO_SERVICE));

	}

	protected void onPause() {
		this.timer.cancel();
		this.mRingTone.stop();
		this.vibrate.cancel();
		// sendBroadcast(new Intent("alert.intent.action.CANCEL"));
		super.onPause();
	}

	protected void onResume() {
		super.onResume();
		setRingtone();

		this.timer = new Timer();
		this.timer.schedule(
				new CallContactTask(),
				1000 * Integer.valueOf(
						mSharedPreferences.getString("Timeout", "5"))
						.intValue());

		startFlash();
		startVibrate();
		this.mRingTone.play();
		for (int i = 0;; i++) {
			if (i >= 7)
				return;
			this.ringerVolume.adjustVolume(1, 0);
		}
	}

	private class CallContactTask extends TimerTask {

		private CallContactTask() {
		}

		private void makeCall() {
			Intent localIntent = new Intent("android.intent.action.CALL");
			localIntent.setData(Uri.parse("tel:"
					+ AlertActivity.this.callphoneSharedPreferences.getString(
							"phonenumber", "")));
			AlertActivity.this.audio.setSpeakerphoneOn(true);
			AlertActivity.this.startActivity(localIntent);
		}

		private void sendSMS() {
			// SmsManager localSmsManager = SmsManager.getDefault();
			// PendingIntent localPendingIntent1 = PendingIntent.getBroadcast(
			// getBaseContext(), 0, new Intent("SMS_SENT"), 0);
			// PendingIntent localPendingIntent2 = PendingIntent.getBroadcast(
			// getBaseContext(), 0, new Intent("SMS_DELIVERED"), 0);
			// String str = smsSharedPreferences.getString("smstext", "") +
			// "http://maps.google.com/maps?q=";
			//
			// + gpsSharedPreferences
			// .getString("gpslatitude", "0") + ","
			// + gpsSharedPreferences.getString("gpslongitude", "0");
			// localSmsManager.sendTextMessage(AlertActivity.this.smsSharedPreferences.getString("sm sphonenumber",
			// ""), null, str,localPendingIntent1, localPendingIntent2);

			SmsManager localSmsManager = SmsManager.getDefault();
			// String str = AlertActivity.this.getString(2131296283);

			String str = smsSharedPreferences.getString("smstext", "")
					+ "http://maps.google.com/maps?q=";

			localSmsManager.sendTextMessage(
					AlertActivity.this.smsSharedPreferences.getString(
							"smsphonenumber", ""), null, str, PendingIntent
							.getBroadcast(getApplicationContext(), 0,
									new Intent(), 0), null);

		}

		public void run() {
			AlertActivity.this.timer.cancel();
			try {
				// String str1 = AlertActivity.this.getString(2131296289);
				// String str1 = "Contact";
				String phonenumberstr = "phonenumber";
				String smsnumberstr = "smsphonenumber";
				if (PhoneNumberUtils
						.isGlobalPhoneNumber(callphoneSharedPreferences
								.getString(phonenumberstr, ""))) {
					String str3 = "makecall";
					if (AlertActivity.this.callphoneSharedPreferences
							.getBoolean(str3, true))
						makeCall();
				}
				if (PhoneNumberUtils.isGlobalPhoneNumber(smsSharedPreferences
						.getString(smsnumberstr, ""))) {
					String str2 = "makesms";
					if (AlertActivity.this.smsSharedPreferences.getBoolean(
							str2, true))
						sendSMS();
				}
				return;
			} catch (Exception localException) {
				Log.w(AlertActivity.TAG, "Failed to invoke call",
						localException);
				return;
			} finally {
				AlertActivity.this.finish();
			}
		}
	}
}