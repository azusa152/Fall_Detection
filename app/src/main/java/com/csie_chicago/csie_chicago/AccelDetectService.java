package com.csie_chicago.csie_chicago;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class AccelDetectService extends Service implements SensorEventListener {
	private SharedPreferences OnOffPerferences ;
	private float[] acceleration = new float[3];
	private float[] lpfAndDevOutput = new float[3];
	private LowPassFilter lpfAndDev;
	private boolean plotLPFAndDev = false;
	private static float AND_DEV_STATIC_ALPHA = 0.0F;
	private final IBinder binder = new ServiceBinder();
	public ArrayList<AccelData>[] sensorData;
	private SensorManager sensorManager;
	private boolean recordStart = false;
	private long now = 0;
	private long timeDiff = 0;
	private long lastUpdate;
	private long lastShake, markShake;
	private double lastX = 0;
	private double lastY = 0;
	private double lastZ = 0;
	private double rawlastX = 0;
	private double rawlastY = 0;
	private double rawlastZ = 0;
	private double force = 0;
	private static double threshold = 0.3d;
	private static double interval = 1.0;
	private int forceCount = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		OnOffPerferences = getSharedPreferences("OnOffstate", Activity.MODE_PRIVATE);
		SharedPreferences.Editor stateEditor = OnOffPerferences.edit();
		stateEditor.putBoolean("is_monitoring", true);
		stateEditor.commit();
//		Intent localIntent = new Intent("ACTION_STATECHANGE");
//	     localIntent.putExtra("currentstate", 1);
//	     sendBroadcast(localIntent);

		lpfAndDev = new LPFAndroidDeveloper();
		lpfAndDev.setAlphaStatic(this.plotLPFAndDev);
		lpfAndDev.setAlpha(AND_DEV_STATIC_ALPHA);

		sensorData = new ArrayList[2];
		for (int i = 0; i < sensorData.length; i++)
			sensorData[i] = new ArrayList<AccelData>();

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	public void onDestroy() {
		sensorManager.unregisterListener(this);
		SharedPreferences.Editor stateEditor = OnOffPerferences.edit();
		stateEditor.putBoolean("is_monitoring",false);
		stateEditor.commit();
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		lastShake = 0;
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		System.arraycopy(event.values, 0, this.acceleration, 0,event.values.length);
		lpfAndDevOutput = lpfAndDev.addSamples(this.acceleration);
		double x = Math.round(lpfAndDevOutput[0] * 100d) / 100d;
		double y = Math.round(lpfAndDevOutput[1] * 100d) / 100d;
		double z = Math.round(lpfAndDevOutput[2] * 100d) / 100d;
		double RawX = acceleration[0];
		double RawY = acceleration[1];
		double RawZ = acceleration[2];
		now = event.timestamp;
		AccelData acceldata = new AccelData(now, x, y, z);
		AccelData Raw_acceldata = new AccelData(now, RawX, RawY, RawZ);

		if (lastUpdate == 0) {

			lastUpdate = now;
			lastShake = 0;
			lastX = x;
			lastY = y;
			lastZ = z;
			rawlastX = RawX;
			rawlastY = RawY;
			rawlastZ = RawZ;
		} else {

			timeDiff = now - lastUpdate;
			if (timeDiff > 0) {
				// force = Math.abs(x + y + z - lastX - lastY - lastZ) /
				// timeDiff;
				// force =Math.abs(Math.sqrt(RawX*RawX+RawY*RawY+RawZ*RawZ)-Math.sqrt(rawlastX*rawlastX+rawlastY*rawlastY+rawlastZ*rawlastZ));
				// force = Math.abs(x + y + z - lastX - lastY - lastZ);
				// force= Math.abs(y-lastY);
				force = (Math.sqrt(RawX * RawX + RawY * RawY + RawZ * RawZ) / 9.80665d) - 1.0d;
				if (Double.compare(force, threshold) > 0) {
					if (recordStart) {
						sensorData[0].add(new AccelData(lastShake, lastX,
								lastY, lastZ));
						sensorData[1].add(new AccelData(lastShake, rawlastX,
								rawlastY, rawlastZ));
						recordStart = false;
					}
					sensorData[0].add(acceldata);
					sensorData[1].add(Raw_acceldata);
					forceCount = 0;
					lastShake = now;
					markShake = now;
				} else {
					if (lastShake == lastUpdate) {
						forceCount += 1;
						sensorData[0].add(acceldata);
						sensorData[1].add(Raw_acceldata);
						lastShake = now;

						if (now - markShake >= (interval * 1.0E+009F)	&& forceCount >= 14) {
							sensorManager.unregisterListener(this);
//							Intent localIntent = new Intent("ACTION_STATECHANGE");
//						     localIntent.putExtra("currentstate", 2);
//						     getBaseContext().sendBroadcast(localIntent);
							/**
							 * bindService��k���d���
							 */
							 Intent broadcastIntent = new Intent("com.csie_chicago.AbnmlProcsBroRec");
							 sendBroadcast(broadcastIntent);
							/**
							 * startService��k���d���
							 */
//							 Intent broadcastIntent = new Intent("edu.mosense.service.AbnormalDataProcessReceive");
//							 broadcastIntent.putExtra("sensorData", sensorData[0]);
//							 broadcastIntent.putExtra("sensorDataRaw", sensorData[1]);
//							 sendBroadcast(broadcastIntent);
						}
					}

				}
				lastX = x;
				lastY = y;
				lastZ = z;
				lastUpdate = now;
			} else {
				Toast.makeText(getBaseContext(), "No Motion detected",
						Toast.LENGTH_SHORT).show();
			}
		}

		String accelerometer = "加速度\n" + "X:" + RawX + "\n" + "Y:" + RawY+ "\n" + "Z:" + RawZ + "\n" + "force:" + force + "\n";
		Log.d("AccelService", accelerometer);
	}

	public class ServiceBinder extends Binder {
		public AccelDetectService getService() {
			return AccelDetectService.this;
		}

		public ArrayList<AccelData> getSensorData() {
			return sensorData[0];
		}

		public ArrayList<AccelData> getRawSensorData() {
			return sensorData[1];
		}
	}
}
