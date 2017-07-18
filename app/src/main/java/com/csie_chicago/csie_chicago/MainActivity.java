package com.csie_chicago.csie_chicago;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends Activity {
    private Button training_Data_Collectbtn;
    private Button fall_Detectionbtn;
    private Button accel_detect_starbtn;
    private Button accel_detect_stopbtn;
    private Button optionbtn;
    private Button alertbtn;
    private TextView accel_text;
    public ArrayList<AccelData> sensorData;
    private boolean isBound = false;


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
        setContentView(R.layout.activity_main);

        training_Data_Collectbtn = (Button) findViewById(R.id.train_data);
        fall_Detectionbtn = (Button) findViewById(R.id.fall_detection);
        accel_detect_starbtn = (Button) findViewById(R.id.accel_start);
        accel_detect_stopbtn = (Button) findViewById(R.id.accel_stop);
        optionbtn = (Button) findViewById(R.id.option);
        alertbtn = (Button) findViewById(R.id.alert);

        training_Data_Collectbtn
                .setOnClickListener(training_data_collect_listener);
        fall_Detectionbtn.setOnClickListener(fall_detection_listener);
        alertbtn.setOnClickListener(alert_listener);
        optionbtn.setOnClickListener(option_listener);

        accel_detect_starbtn
                .setOnClickListener(laccel_detect_servicestar_listener);
        accel_detect_stopbtn
                .setOnClickListener(laccel_detect_servicestop_listener);
        accel_detect_stopbtn.setEnabled(isBound);

    }


    OnClickListener fall_detection_listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent it = new Intent(getBaseContext(), FallDetection.class);
            startActivity(it);

            overridePendingTransition(R.anim.slide_left_in,R.anim.slide_right_out);
        }
    };

    OnClickListener training_data_collect_listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent it = new Intent(getBaseContext(), TrainingDataCollect.class);
            startActivity(it);

            overridePendingTransition(R.anim.slide_left_in,R.anim.slide_right_out);
        }
    };

    OnClickListener alert_listener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent it = new Intent(getBaseContext(), AudioSetting.class);
            startActivity(it);

            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        }
    };
    OnClickListener option_listener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent it = new Intent(getBaseContext(), Option.class);
            startActivity(it);

            overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
        }
    };
    OnClickListener laccel_detect_servicestar_listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this,
                    com.csie_chicago.csie_chicago.AccelDetectService.class);
            if (!isBound) {
                bindService(intent, serviceCon, Context.BIND_AUTO_CREATE);
                accel_detect_starbtn.setEnabled(isBound);
                isBound = true;
                accel_detect_stopbtn.setEnabled(isBound);
            }

        }
    };
    OnClickListener laccel_detect_servicestop_listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isBound) {
                accel_detect_starbtn.setEnabled(isBound);
                isBound = false;
                accel_detect_stopbtn.setEnabled(isBound);

                StringBuilder Y_value = new StringBuilder();
                for (int num = 0; num < sensorData.size(); num++) {
                    Y_value.append(sensorData.get(num).getY() + " ,\t");
                    accel_text.setText(Y_value);
                }
                unbindService(serviceCon);
                Log.d("accel value", Y_value.toString());
            }
        }

    };

    private ServiceConnection serviceCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sensorData = ((AccelDetectService.ServiceBinder) service)
                    .getSensorData();
            Toast.makeText(MainActivity.this, "服務啟動成功 ", Toast.LENGTH_LONG)
                    .show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(MainActivity.this, "啟動服務失敗", Toast.LENGTH_LONG)
                    .show();
        }
    };
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            ConfirmExit();//按返回鍵，則執行退出確認
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void ConfirmExit(){//退出確認
        AlertDialog.Builder ad=new AlertDialog.Builder(MainActivity.this);
        ad.setTitle("離開");
        ad.setMessage("確定要離開?");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() {//退出按鈕
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub
                MainActivity.this.finish();//關閉activity

            }
        });
        ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                //不退出不用執行任何操作
            }
        });
        ad.show();//示對話框
    }
}
