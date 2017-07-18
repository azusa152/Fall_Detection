package com.csie_chicago.csie_chicago;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class GPSService extends Service {
	private LocationManager locationManager;
	private Location myLocation;
	private Criteria criteria;
	private SharedPreferences gpsPerferences ;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		gpsPerferences = getSharedPreferences("GPSsetting", Activity.MODE_PRIVATE);
		// 取得LocationManager物件
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		// 建立Criteria規則物件，並要求精準的定位功能
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);

		// 依據criteria的規則回傳最適合的定位名稱，
		// true代表只回傳目前可提供的定位名稱
		String provider = locationManager.getBestProvider(criteria, true);

		// 利用指定的定位名稱來取得自己最新位置
		Location myLocation = locationManager.getLastKnownLocation(provider);
		this.myLocation = myLocation;
		// updateMyLocationInfo(myLocation);
		
		// 建議要取得位置資訊時都讓系統重新擷取最適當的定位方式
		String provider1 = locationManager.getBestProvider(criteria, true);

		// 可以設定經過多少毫秒或多少公尺後透過LocationListener來監控自己位置是否改變
		locationManager.requestLocationUpdates(provider1, 1, 0,
				myLocationListener);

	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		locationManager.removeUpdates(myLocationListener);
	}

	/**
	 * GPS監聽器
	 * 
	 * @param location
	 */
	private LocationListener myLocationListener = new LocationListener() {

		@Override
		// 位置改變時呼叫並傳入新的位置(Location物件)
		public void onLocationChanged(Location location) {
			myLocation = location;
			updateMyLocationInfo(location);
		}

		@Override
		// 定位功能改變時呼叫，provider-定位名稱(例如gps或network)；status-狀態
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			case LocationProvider.AVAILABLE:
				Toast.makeText(getBaseContext(), provider + "可以使用",
						Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.OUT_OF_SERVICE:
				Toast.makeText(getBaseContext(),
						provider + "無法提供服務，而且短時間內無法回復", Toast.LENGTH_SHORT)
						.show();
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Toast.makeText(getBaseContext(), provider + "暫時無法使用，但應該很快就可回復",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}

		@Override
		// 使用者將定位功能開啟時呼叫
		public void onProviderEnabled(String provider) {
			Toast.makeText(getBaseContext(), provider + "功能已經開啓",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		// 使用者將定位功能關閉時呼叫
		public void onProviderDisabled(String provider) {
			Toast.makeText(getBaseContext(), provider + "功能已經關閉",
					Toast.LENGTH_SHORT).show();
		}

	};

	private void updateMyLocationInfo(Location myLocation) {
		try {

			SharedPreferences.Editor gpsEditor = gpsPerferences.edit();
			String Latitude = Double.valueOf(myLocation.getLatitude())
					.toString();
			String Longitude = Double.valueOf(myLocation.getLongitude())
					.toString();
			gpsEditor.putString("gpslatitude", Latitude);
			gpsEditor.putString("gpslongitude", Longitude);
			gpsEditor.commit();
		} catch (NullPointerException ex) {

		}

	}
}
