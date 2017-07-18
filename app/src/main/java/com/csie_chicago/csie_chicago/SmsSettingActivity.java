package com.csie_chicago.csie_chicago;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsSettingActivity extends AbstractBasePreferencesActivity{
	private EditText editSmsPhoneNumber,editSmsText;
	private ListView listView;
	private List<Map<String, Object>> items;
	private Cursor people;
	private Button smsbtn;
	private CheckBox smsckb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_setting);
		smsbtn = (Button) findViewById(R.id.imagesms);
		smsckb = (CheckBox) findViewById(R.id.smscheck);
		editSmsPhoneNumber = (EditText) findViewById(R.id.smsphonenumber);
		editSmsText = (EditText)findViewById(R.id.smsedittext);
		listView = (ListView) findViewById(android.R.id.list);
		items = new ArrayList<Map<String, Object>>();

		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER };
		people = getContentResolver().query(uri, projection, null, null, null);

		int indexName = people
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		int indexNumber = people
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

		people.moveToFirst();
		do {

			String name = people.getString(indexName);
			String number = people.getString(indexNumber);
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("name", name);
			item.put("id", number);
			items.add(item);

		} while (people.moveToNext());

		SimpleAdapter simpleAdapter = new SimpleAdapter(this, items,
				R.layout.simplelistview, new String[] { "name", "id" },
				new int[] { R.id.phonename, R.id.phonenumber });

		listView.setAdapter(simpleAdapter);

		listView.setOnItemClickListener(listviewOnItemClkLis);
		smsbtn.setOnClickListener(smsClklLis);
		smsckb.setOnCheckedChangeListener(smsChgLis);
	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences callphonePerferences = getSharedPreferences(
				"smssetting", Activity.MODE_PRIVATE);

		if (callphonePerferences.getBoolean("makesms", false)) {
			smsckb.setChecked(true);
		} else {
			smsckb.setChecked(false);
		}
		editSmsPhoneNumber.setText(callphonePerferences.getString("smsphonenumber",""));

	}

	AdapterView.OnItemClickListener listviewOnItemClkLis = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			String phonenumber = ((TextView) arg1
					.findViewById(R.id.phonenumber)).getText().toString();
			editSmsPhoneNumber.setText(phonenumber);
		}
	};
	View.OnClickListener smsClklLis = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			SharedPreferences smsPerferences = getSharedPreferences("smssetting", Activity.MODE_PRIVATE);
			SharedPreferences.Editor smsEditor = smsPerferences.edit();
			String smsphonenumber = editSmsPhoneNumber.getText().toString().trim();
			String smstext = editSmsText.getText().toString().trim();
			smsEditor.putString("smsphonenumber", smsphonenumber);
			smsEditor.putString("smstext", smstext);
			smsEditor.commit();
			Toast.makeText(getBaseContext(), "簡訊內容與連絡人設定完成  " ,Toast.LENGTH_SHORT).show();
		}
	};

	OnCheckedChangeListener smsChgLis = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
			if (isChecked) {
				SharedPreferences smsPerferences = getSharedPreferences("smssetting", Activity.MODE_PRIVATE);
				SharedPreferences.Editor smsEditor = smsPerferences.edit();
				smsEditor.putBoolean("makesms", true);
				smsEditor.commit();
				smsbtn.setEnabled(true);
				Toast.makeText(getBaseContext(), "開起發送簡訊", Toast.LENGTH_SHORT).show();
			} else {
				SharedPreferences smsPerferences = getSharedPreferences(
						"smssetting", Activity.MODE_PRIVATE);
				SharedPreferences.Editor smsEditor = smsPerferences
						.edit();
				smsEditor.putBoolean("makesms", false);
				smsbtn.setEnabled(false);
				smsEditor.commit();
				Toast.makeText(getBaseContext(), "關閉發送簡訊 ", Toast.LENGTH_SHORT)
						.show();
			}
		}

	};
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
