package com.csie_chicago.csie_chicago;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class TrainingDataCollect extends Activity implements
		SensorEventListener, OnClickListener {
	private SensorManager sensorManager;
	private Button btnStart, btnStop, btnRsult;
	private TextView actext;
    private ImageView sback;
	private boolean started = false;// 加速度計開始偵測的布林紀錄值.
	private ArrayList<AccelData>[] sensorData;
	private LinearLayout layout;
	private View mChart;
	private int number;
	private LowPassFilter lpfAndDev;
	private boolean plotLPFAndDev = false;
	private static float AND_DEV_STATIC_ALPHA = 0.0F;
	private float[] acceleration = new float[3];
	private float[] lpfAndDevOutput = new float[3];
	private String dir;
	public static String AppDirectory = "csie_chicago";//放記錄資料夾名稱
	private OutputStream outStream;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fall);

		this.lpfAndDev = new LPFAndroidDeveloper(); /// 低通
		this.lpfAndDev.setAlphaStatic(this.plotLPFAndDev);
		this.lpfAndDev.setAlpha(AND_DEV_STATIC_ALPHA);
		layout = (LinearLayout) findViewById(R.id.chart_container);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorData = new ArrayList[3];
		for (int i = 0; i < sensorData.length; i++)
			sensorData[i] = new ArrayList<AccelData>();
		actext = (TextView) findViewById(R.id.tvMsg);
		btnStart = (Button) findViewById(R.id.btnStart);
		btnStop = (Button) findViewById(R.id.btnStop);
		btnRsult = (Button) findViewById(R.id.btnRsult);
        sback=(ImageView)findViewById(R.id.imageView);
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		btnRsult.setOnClickListener(this);
		btnStart.setEnabled(true);
		btnStop.setEnabled(false);
		btnRsult.setEnabled(false);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (started == true) {
			sensorManager.unregisterListener(this);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnStart:
			number += 1;
            sback.setBackgroundColor(Color.parseColor("#F06292") );
			btnStart.setEnabled(false);
			btnStop.setEnabled(true);
			sensorData[0] = new ArrayList<AccelData>();
			started = true;
			Sensor accel = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sensorManager.registerListener(this, accel,
					SensorManager.SENSOR_DELAY_UI);
			break;

		case R.id.btnStop:
            sback.setBackgroundColor(Color.parseColor("#AED581") );
			btnRsult.setEnabled(true);
			btnStart.setEnabled(true);
			btnStop.setEnabled(false);
			started = false;
			sensorManager.unregisterListener(this);
			layout.removeAllViews();
			break;

		case R.id.btnRsult:
            layout.setBackgroundColor(000000);
            sback.setBackgroundColor(Color.parseColor("#BBDEFB") );
			StringBuilder[] s1;
			btnRsult.setEnabled(false);
			sensorData[0] = Normalization(sensorData[0]);//// 正規化sensorData[0]
            s1 = featureExtra_111(sensorData[0]);//s1為字串化的sensorData[0]
			s1[0] = removeChr(s1[0].toString(), 'X');// 移除x(error的值)
			s1[1] = removeChr(s1[1].toString(), 'X');
			s1[2] = removeChr(s1[2].toString(), 'X');
			String ss;
			ss = "(紅X-axi):" + s1[0].toString() + "\n" + "(黑Y-axi):"
					+ s1[1].toString() + "\n" + "(藍Z-axi):" + s1[2].toString()
					+ "\n";
			actext.setText(ss);
			String str1 = "---------------------------\n";
			/*
			 * sensorData[0]為正規化且低通過的(timestamp, x, y, z)，
			 * sensorData[1]為低通過的(timestamp, x, y, z)， sensorData[2]為(timestamp,
			 * Math.round(event.values[0] * 100d) / 100d,
			 * Math.round(event.values[1] * 100d) / 100d,
			 * Math.round(event.values[2] * 100d) / 100d);為原始值
			 */
			for (int i = 0; i < sensorData[1].size(); i++) {
                // 低通
                str1 += sensorData[1].get(i).getX() + "          "
                        + sensorData[1].get(i).getY() + "          "
                        + sensorData[1].get(i).getZ() + "          "
                        + sensorData[1].get(i).getTimestamp() * 1.0E-009F
                        + "\n";
			}
			String str2 = "---------------------------\n";
			for (int i = 0; i < sensorData[0].size(); i++) {
                // 低通且正規化
                str2 += sensorData[0].get(i).getX() + "          "
                        + sensorData[0].get(i).getY() + "          "
                        + sensorData[0].get(i).getZ() + "          "
                        + sensorData[0].get(i).getTimestamp() * 1.0E-009F
                        + "\n";
			}

			String str3 = "---------------------------\n";
			for (int i = 0; i < sensorData[2].size(); i++) {
                // 原始
                str3 += sensorData[2].get(i).getX() + "          "
                        + sensorData[2].get(i).getY() + "          "
                        + sensorData[2].get(i).getZ() + "          "
                        + sensorData[2].get(i).getTimestamp() * 1.0E-009F
                        + "\n";
			}

            String str = ss + str3 + str1 + str2;
            this.dir = (Environment.getExternalStorageDirectory().toString()// 找sd卡路徑
                    + "/" + AppDirectory);
			String Filename = getPhotoFileName();
			File localFile = new File(this.dir);
			if (!localFile.exists()) {// 先判斷目錄存不存在
				localFile.mkdirs();
			}
			File localFile2 = new File(this.dir, Filename);
			try {
				outStream = new BufferedOutputStream(new FileOutputStream(
						localFile2));
				this.outStream.write(str.getBytes());
				this.outStream.flush();
				outStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			openChart();

			sensorData[0].clear();
			sensorData[1].clear();
			sensorData[2].clear();
			break;
		default:
			break;
		}
	}

	private void writeToSD(float[] paramArrayOfFloat, long paramLong) {
		String str = null;
		if (this.outStream != null) {
			paramArrayOfFloat[0] = ((float) ((int) (1000.0D * paramArrayOfFloat[0]) / 1000.0D));
			paramArrayOfFloat[1] = ((float) ((int) (1000.0D * paramArrayOfFloat[1]) / 1000.0D));
			paramArrayOfFloat[2] = ((float) ((int) (1000.0D * paramArrayOfFloat[2]) / 1000.0D));
			str = paramArrayOfFloat[0] + " " + paramArrayOfFloat[1] + " "
					+ paramArrayOfFloat[2] + " " + paramLong + "\n";
		}
		try {
			this.outStream.write(str.getBytes());
			this.outStream.flush();
			return;
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
	}

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'AccDATA'_yyyyMMddHHmmss");
		return dateFormat.format(date) + ".txt";
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		System.arraycopy(event.values, 0, this.acceleration, 0,
				event.values.length);
		lpfAndDevOutput = lpfAndDev.addSamples(this.acceleration);
		StringBuilder sensorInfo = new StringBuilder();
		sensorInfo.append("sensor name: " + event.sensor.getName() + "\n");
		sensorInfo.append("sensor type: " + event.sensor.getType() + "\n");
		sensorInfo.append("used power: " + event.sensor.getPower() + " mA\n");
		sensorInfo.append("sensor range: " + event.sensor.getMaximumRange()
				+ "m^2/s\n");
		actext.setText(sensorInfo);
		if (started) {
			double x = Math.round(lpfAndDevOutput[0] * 100d) / 100d;
			double y = Math.round(lpfAndDevOutput[1] * 100d) / 100d;
			double z = Math.round(lpfAndDevOutput[2] * 100d) / 100d;
			long timestamp = event.timestamp;
			AccelData data = new AccelData(timestamp, x, y, z);
			AccelData data2 = new AccelData(timestamp, x, y, z);
			AccelData data3 = new AccelData(timestamp,
					Math.round(event.values[0] * 100d) / 100d,
					Math.round(event.values[1] * 100d) / 100d,
					Math.round(event.values[2] * 100d) / 100d);
			sensorData[0].add(data);
			sensorData[1].add(data2);
			sensorData[2].add(data3);

		}
	}

	private void openChart() {//繪圖

		if (sensorData != null || sensorData[0].size() > 0) {
			long t = sensorData[0].get(0).getTimestamp();

			XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
			XYSeries xSeries = new XYSeries("X");
			XYSeries ySeries = new XYSeries("Y");
			XYSeries zSeries = new XYSeries("Z");

			for (AccelData data : sensorData[0]) {
				xSeries.add(data.getTimestamp() - t, data.getX());
				ySeries.add(data.getTimestamp() - t, data.getY());
				zSeries.add(data.getTimestamp() - t, data.getZ());
			}

			dataset.addSeries(xSeries);
			dataset.addSeries(ySeries);
			dataset.addSeries(zSeries);

			XYSeriesRenderer xRenderer = new XYSeriesRenderer();
			xRenderer.setColor(Color.RED);
			xRenderer.setPointStyle(PointStyle.CIRCLE);
			xRenderer.setFillPoints(true);
			xRenderer.setLineWidth(1);
			xRenderer.setDisplayChartValues(true);

			XYSeriesRenderer yRenderer = new XYSeriesRenderer();
			yRenderer.setColor(Color.BLACK);
			yRenderer.setPointStyle(PointStyle.CIRCLE);
			yRenderer.setFillPoints(true);
			yRenderer.setLineWidth(1);
			yRenderer.setDisplayChartValues(true);

			XYSeriesRenderer zRenderer = new XYSeriesRenderer();
			zRenderer.setColor(Color.BLUE);
			zRenderer.setPointStyle(PointStyle.CIRCLE);
			zRenderer.setFillPoints(true);
			zRenderer.setLineWidth(1);
			zRenderer.setDisplayChartValues(true);

			XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
			multiRenderer.setXLabels(0);
			multiRenderer.setLabelsColor(Color.RED);
			multiRenderer.setChartTitle("t vs (x,y,z)");
			multiRenderer.setXTitle("Sensor Data");
			multiRenderer.setYTitle("Values of Acceleration");
			multiRenderer.setZoomButtonsVisible(true);

			for (int i = 0; i < sensorData[0].size(); i++) {
				int gg = (int) (sensorData[0].get(i).getTimestamp() - t);
				multiRenderer.addXTextLabel(i + 1, "" + gg);
			}

			for (int i = 0; i < 12; i++) {
				multiRenderer.addYTextLabel(i, "" + i);
			}

			multiRenderer.addSeriesRenderer(xRenderer);
			multiRenderer.addSeriesRenderer(yRenderer);
			multiRenderer.addSeriesRenderer(zRenderer);

			// Getting a reference to LinearLayout of the MainActivity Layout
			// Creating a Line Chart
			mChart = ChartFactory.getLineChartView(getBaseContext(), dataset,
					multiRenderer);

			// Adding the Line Chart to the LinearLayout
			layout.addView(mChart);

		}
	}

	private ArrayList<AccelData> Normalization(ArrayList<AccelData> acceldata) {
		double maxX = acceldata.get(0).getX();
		double maxY = acceldata.get(0).getY();
		double maxZ = acceldata.get(0).getZ();
/*
		 * 找出陣列裡最大數值
		 */
		for (int num = 1; num < acceldata.size(); num++) {
			double tempX = Math.abs(acceldata.get(num).getX());
			if (tempX > maxX) {
				maxX = tempX;
			}
		}
		for (int num = 1; num < acceldata.size(); num++) {
			double tempY = Math.abs(acceldata.get(num).getY());
			if (tempY > maxY) {
				maxY = tempY;
			}
		}
		for (int num = 1; num < acceldata.size(); num++) {
			double tempZ = Math.abs(acceldata.get(num).getZ());
			if (tempZ > maxZ) {
				maxZ = tempZ;
			}
		}
		/*
		 * 正規化加速度數值 1~100
		 */
		for (int num = 0; num < acceldata.size(); num++) {
			double tempX = acceldata.get(num).getX() / maxX;
			tempX = Math.round(tempX * 100d);
			acceldata.get(num).setX(tempX);
		}

		for (int num = 0; num < acceldata.size(); num++) {
			double tempY = acceldata.get(num).getY() / maxY;
			tempY = Math.round(tempY * 100d);
			acceldata.get(num).setY(tempY);
		}

		for (int num = 0; num < acceldata.size(); num++) {
			double tempZ = acceldata.get(num).getZ() / maxZ;
			tempZ = Math.round(tempZ * 100d);
			acceldata.get(num).setZ(tempZ);
		}

		return acceldata;

	}

	private StringBuilder[] featureExtra_111(ArrayList<AccelData> acceldata) {

		StringBuilder[] eigenvalue = new StringBuilder[3];
		for (int i = 0; i < eigenvalue.length; i++)
			eigenvalue[i] = new StringBuilder();

		for (int i = 1; i < acceldata.size(); i++) {
			double temp_new_X = acceldata.get(i).getX();
			double temp_past_X = acceldata.get(i - 1).getX();
			double differenceX = temp_new_X - temp_past_X;
			double temp_new_Y = acceldata.get(i).getY();
			double temp_past_Y = acceldata.get(i - 1).getY();
			double differenceY = temp_new_Y - temp_past_Y;
			double temp_new_Z = acceldata.get(i).getZ();
			double temp_past_Z = acceldata.get(i - 1).getZ();
			double differenceZ = temp_new_Z - temp_past_Z;

			String x, y, z;
			x = findCharSymbol(differenceX);
			y = findCharSymbol(differenceY);
			z = findCharSymbol(differenceZ);

			eigenvalue[0].append(x);
			eigenvalue[1].append(y);
			eigenvalue[2].append(z);
		}
		return eigenvalue;
	}

	private String findCharSymbol(double difference) {

		if (difference > 84) {
			return "H";
		} else if (difference > 72) {
			return "G";
		} else if (difference > 60) {
			return "F";
		} else if (difference > 48) {
			return "E";
		} else if (difference > 36) {
			return "D";
		} else if (difference > 24) {
			return "C";
		} else if (difference > 12) {
			return "B";
		} else if (difference >= 5) {
			return "A";
		} else if (difference <= -5 & difference > -12) {
			return "L";
		} else if (difference < -12 & difference > -24) {
			return "M";
		} else if (difference < -24 & difference > -36) {
			return "N";
		} else if (difference < -36 & difference > -48) {
			return "O";
		} else if (difference < -48 & difference > -60) {
			return "P";
		} else if (difference < -60 & difference > -72) {
			return "Q";
		} else if (difference < -72 & difference > -84) {
			return "R";
		} else if (difference < -84) {
			return "S";
		} else {
			return "X";
		}
	}

	private StringBuilder removeChr(String str, char x) {
		StringBuilder strBuilder = new StringBuilder();
		char[] rmString = str.toCharArray();
		for (int i = 0; i < rmString.length; i++) {
			if (rmString[i] == x) {

			} else {
				strBuilder.append(rmString[i]);
			}
		}
		return strBuilder;
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
