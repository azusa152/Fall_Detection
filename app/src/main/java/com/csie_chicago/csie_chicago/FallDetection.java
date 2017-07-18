package com.csie_chicago.csie_chicago;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FallDetection extends Activity {
    private static final int NOTI_ID = 100;
    private static ImageView mFallStatusImage;
    private SharedPreferences mSharedPreferences;
    private Button starbtn;
    private Button stopbtn;
    private TextView accel_text;
    private ImageView sback;
    private EditText  falltimetxt ,squattimetxt ,sittimetxt ,jumptimetxt,othertxt;
    private int fallcount=0,othercount=0;
    public ArrayList<AccelData> sensorData;
    public ArrayList<AccelData> RawSensorData;
    public ArrayList<AccelData> NormalizationSensorData;
    private Intent accelDetectServiceIntent ;

    private boolean isBound = false;
    private String charSymbol;
    private String[] abnmlLcsTrain = new String[4];
    private String[] abnmlLcsTrain2 = new String[4];
    private String[] trainData=new String[4];
    private String[] trainData2=new String[4];
    private int[] trainDifNum = new int[4];
    private int[] trainDifNum2 = new int[4];
    private int[] trainDeviSum = new int[4];
    private int[] trainDeviSum2 = new int[4];
    private double[] trainDataScore = new double[4];
    private double[] trainDataScore2 = new double[4];
    private double[] trainAvgLocation = new double[4];
    private double[] trainAvgLocation2 = new double[4];

    private SimilarCalculate[]  similarCalculate = new SimilarCalculate[4];
    private SimilarCalculateMerge[]  similarCalculateMerge = new SimilarCalculateMerge[4];

    private double[] similarValue = new double[4];
    private double[] similarValueMerge = new double[4];
    private int[] deviSumDif = new int[4];
    private double[] rotationAngle,rotationAnglePitch,rotationAngleRoll;
    private double angleVariation ,pitchAngleVariation,rollAngleVariation;

    private ApproxLCS trainXcharSymbolData,trainXcharSymbolData2 ;
    private RotationAngle rotationAngleObject;
    private int PositionSumSubAvgLocation,PositionSumSubAvgLocation2;
    private int PositionSum,PositionSum2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fallone);
        mFallStatusImage = (ImageView)findViewById(R.id.img_fall_status);
        mSharedPreferences = getSharedPreferences("audiosetting",Activity.MODE_PRIVATE);
        registerReceiver(AbnmlProcsBroRec, new IntentFilter("com.csie_chicago.AbnmlProcsBroRec"));
        accelDetectServiceIntent = new Intent(FallDetection.this,com.csie_chicago.csie_chicago.AccelDetectService.class);
        trainData[0]="\n" +"LLMOLMAAA";
        trainData2[0]="LLLACBNMLBA";
        trainDifNum[0]=4;
        trainDifNum2[0]=6;
        trainDeviSum[0]=41;
        trainDeviSum[02]=39;
        trainDataScore[0]=656;
        trainDataScore2[0]=788;
        trainAvgLocation[0]=17;
        trainAvgLocation2[0]=17;

        starbtn = (Button) findViewById(R.id.DFstarbtn);
        stopbtn = (Button) findViewById(R.id.DFstopbtn);
        accel_text = (TextView) findViewById(R.id.textView2);
        accel_text.setMovementMethod(ScrollingMovementMethod.getInstance());
        falltimetxt = (EditText)findViewById(R.id.falltime);
        othertxt = (EditText)findViewById(R.id.other);
        sback=(ImageView)findViewById(R.id.sback);
        starbtn.setOnClickListener(star_listener);
        stopbtn.setOnClickListener(stop_listener);
        stopbtn.setEnabled(isBound);
    }


    OnClickListener star_listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isBound) {
                boolean ServiceCheck = bindService(accelDetectServiceIntent, serviceCon, Context.BIND_AUTO_CREATE);
                showNotification();

                starbtn.setEnabled(isBound);
                isBound = true;
                stopbtn.setEnabled(isBound);
                falltimetxt.setText("0");
                othertxt.setText("0");
                Toast.makeText(FallDetection.this, "偵測開始", Toast.LENGTH_SHORT).show();
                sback.setBackgroundColor(Color.parseColor("#FFF176") );


            }
        }
    };

    OnClickListener stop_listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isBound) {
                starbtn.setEnabled(isBound);
                isBound = false;
                stopbtn.setEnabled(isBound);

                NotificationManager notiMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notiMgr.cancel(NOTI_ID);

                unbindService(serviceCon);
                Toast.makeText(FallDetection.this, "偵測停止 ", Toast.LENGTH_SHORT).show();
                sback.setBackgroundColor(Color.parseColor("#C5FFFD") );
            }
            fallcount=0;
            othercount=0;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences OnOffPerferences = getSharedPreferences("OnOffstate", Activity.MODE_PRIVATE);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private ServiceConnection serviceCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sensorData = ((AccelDetectService.ServiceBinder) service).getSensorData();
            RawSensorData = ((AccelDetectService.ServiceBinder) service).getSensorData();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(FallDetection.this, "啟動服務失敗", Toast.LENGTH_LONG).show();
        }
    };

    private BroadcastReceiver AbnmlProcsBroRec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.csie_chicago.AbnmlProcsBroRec".equals(intent.getAction())) {
                StringBuilder Y_value = new StringBuilder();

                unbindService(serviceCon);

                rotationAngleObject = new RotationAngle(RawSensorData);
                rotationAngle = rotationAngleObject.getRotationAngle();
                angleVariation =Math.ceil(rotationAngleObject.getAngleVariation());
                rotationAnglePitch = rotationAngleObject.getRotationAnglePitch();
                rotationAngleRoll = rotationAngleObject.getRotationAngleRoll();
                pitchAngleVariation = Math.ceil(rotationAngleObject.getPitchAngleVariation());
                rollAngleVariation = Math.ceil(rotationAngleObject.getRollAngleVariation());
              //  Y_value.append("角度變化量(原始,Roll):"+angleVariation+","+rollAngleVariation+"\n"+"異常偵測資料角度"+"("+rotationAngle.length+"):"+"\n");
                NormalizationSensorData=new Normalization(sensorData).getAcceldatas();
                charSymbol =  removeChr(new FeatureExtra(NormalizationSensorData).getCharSymbol(),'X');
                Y_value.append("異常偵測資料:"+charSymbol+"\n");
                trainXcharSymbolData = new ApproxLCS(trainData[0],charSymbol );
                trainXcharSymbolData2 = new ApproxLCS(trainData2[0],charSymbol );
                abnmlLcsTrain[0] = trainXcharSymbolData.getLcs();
                abnmlLcsTrain2[0] = trainXcharSymbolData2.getLcs();
              //  Y_value.append("\n跌倒結果1:"+abnmlLcsTrain[0]+"\n跌倒結果2:"+abnmlLcsTrain2[0]);
                PositionSum = trainXcharSymbolData.getPositionSum();
                PositionSum2 = trainXcharSymbolData2.getPositionSum();
                PositionSumSubAvgLocation =(int) Math.abs(PositionSum-trainAvgLocation[0]);
                PositionSumSubAvgLocation2 =(int) Math.abs(PositionSum2-trainAvgLocation2[0]);
              //  Y_value.append("\n"+"PositionSum:"+PositionSum+"\t"+"trainAvgLocation:"+trainAvgLocation[0]+"\t"+"PositionSumSubAvgLocation:"+PositionSumSubAvgLocation);
                //Y_value.append("\n"+"PositionSum2:"+PositionSum2+"\t"+"trainAvgLocation2:"+trainAvgLocation2[0]+"\t"+"PositionSumSubAvgLocation2:"+PositionSumSubAvgLocation2);
                similarCalculateMerge[0] =  new SimilarCalculateMerge(abnmlLcsTrain[0],trainData[0] ,trainData[0].length(), trainDeviSum[0],trainDifNum[0], trainDataScore[0]);
                similarValueMerge[0]= Math.ceil(similarCalculateMerge[0].getSimilarMergeValue());
                deviSumDif[0] = similarCalculateMerge[0].getDeviSumDif();
                Y_value.append("\n與跌倒相似度:"+similarValueMerge[0]);

              if(PositionSumSubAvgLocation <= 10){

                    if(similarValueMerge[0]>=75 ){
                        fallcount = fallcount+1;
                        falltimetxt.setText( Integer.toString(fallcount));
                        if(mSharedPreferences.getBoolean("issue_alert", false)){
                            Intent it = new Intent();
                            it.setClass(getBaseContext(), AlertActivity.class);
                            it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(it);
                        }
                    }else{

                        othercount=othercount+1;
                        othertxt.setText(Integer.toString(othercount));

                    }
                    accel_text.setText(Y_value);
                    bindService(accelDetectServiceIntent, serviceCon, Context.BIND_AUTO_CREATE);

              }else {

                    accel_text.setText(Y_value);
                    bindService(accelDetectServiceIntent, serviceCon, Context.BIND_AUTO_CREATE);

                }


            }

        }
        private int findMaxValueLocation(double[] similarValue){
            int i, n2 = 0;
            double max;
            double num[] = similarValue;

            max = num[0];
            for (i = 0; i < num.length; i++) {
                if (num[i] > max) {
                    n2 = i;
                    max = num[i];
                }
            }
            return n2;
        }

        private String  removeChr(String str, char x) {
            StringBuilder strBuilder = new StringBuilder();
            char[] rmString = str.toCharArray();
            for (int i = 0; i < rmString.length; i++) {
                if (rmString[i] == x) {

                } else {
                    strBuilder.append(rmString[i]);
                }
            }
            return strBuilder.toString();
        }

    };
    private void showNotification() {
        Notification noti = new Notification(R.drawable.on_notification,"iFall Aware is ON",System.currentTimeMillis());
        Intent it = new Intent();
        it.setClass(this, FallDetection.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent penIt = PendingIntent.getActivity( this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
        noti.setLatestEventInfo(this, "異常動作偵測", "加速度感測器監測中...", penIt);
        NotificationManager notiMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notiMgr.cancel(NOTI_ID);
        notiMgr.notify(NOTI_ID, noti);
    }
    private class StateChangeReceiver extends BroadcastReceiver
    {
        public static final int OFF = 0;
        public static final int ON = 1;
        public static final int RISKFALL = 2;
        int mState;

        private StateChangeReceiver()
        {
        }

        public void onReceive(Context paramContext, Intent paramIntent)
        {
            this.mState = paramIntent.getIntExtra("currentstate", -1);
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}