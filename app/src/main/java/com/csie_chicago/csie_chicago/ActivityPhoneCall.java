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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class ActivityPhoneCall extends AbstractBasePreferencesActivity {
	private EditText editPhoneNumber;
	private AutoCompleteTextView autoeditPhoneNumber;
	private ListView listView;
	private List<Map<String, Object>> items;
	private Cursor people;
	private Button emergencybtn;
	private CheckBox emergencyckb;
	private String phonenumber,phonename;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_call);
		emergencybtn = (Button) findViewById(R.id.imagePhoneCall);
		emergencyckb = (CheckBox)findViewById(R.id.callcheck);
		//editPhoneNumber = (EditText) findViewById(R.id.editphonenumber);
		autoeditPhoneNumber=(AutoCompleteTextView) findViewById(R.id.autoeditphonenumber);
		
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
		
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,getContactsName());	
		autoeditPhoneNumber.setAdapter(simpleAdapter);
		autoeditPhoneNumber.setOnItemClickListener(autoTextOnItemClkLis);
		listView.setAdapter(simpleAdapter);
		listView.setOnItemClickListener(listviewOnItemClkLis);
		emergencybtn.setOnClickListener(emergencyClklLis);
		emergencyckb.setOnCheckedChangeListener(emergencyChgLis);
	}
	private String[] getContactsName() {
		int c = people.getCount();
		String[] contactsName  = new String[c];
		for (int i = 0; i < contactsName.length; i++) {
			people.moveToPosition(i);
			contactsName[i]=people.getString(0);
		}
		return contactsName;
	}
	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences callphonePerferences = getSharedPreferences("callphonesetting",Activity.MODE_PRIVATE );  
		
		if(callphonePerferences.getBoolean("makecall", false)){
			emergencyckb.setChecked(true);
		}else{
			emergencyckb.setChecked(false);
		}
		//editPhoneNumber.setText(callphonePerferences.getString("phonename", ""));
		autoeditPhoneNumber.setText(callphonePerferences.getString("phonename", ""));
	}
	
	OnItemClickListener autoTextOnItemClkLis=new OnItemClickListener() {
	    @Override
	    public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
	    	phonenumber = ((TextView) view.findViewById(R.id.phonenumber)).getText().toString();
			 phonename = ((TextView) view.findViewById(R.id.phonename)).getText().toString();
			//editPhoneNumber.setText(phonename);
			autoeditPhoneNumber.setText(phonename);
	    	
	    }
	};
	OnItemClickListener listviewOnItemClkLis = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			phonenumber = ((TextView) arg1.findViewById(R.id.phonenumber)).getText().toString();
			 phonename = ((TextView) arg1.findViewById(R.id.phonename)).getText().toString();
			//editPhoneNumber.setText(phonename);
			autoeditPhoneNumber.setText(phonename);

		}
	};
	View.OnClickListener emergencyClklLis = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {

			SharedPreferences callphonePerferences = getSharedPreferences("callphonesetting",Activity.MODE_PRIVATE );  
	        SharedPreferences.Editor callphoneEditor = callphonePerferences.edit();  
	       // String phonenumber = editPhoneNumber.getText().toString().trim();
	        callphoneEditor.putString( "phonenumber" , phonenumber);  
	        callphoneEditor.putString( "phonename" , phonename);  
	        callphoneEditor.commit(); 
	        Toast.makeText(getBaseContext(), "開起撥打電話 "+phonenumber, Toast.LENGTH_LONG).show();
		}
	};
	
	
	OnCheckedChangeListener emergencyChgLis = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
			if ( isChecked )
	        {
				SharedPreferences callphonePerferences = getSharedPreferences("callphonesetting",Activity.MODE_PRIVATE );  
		        SharedPreferences.Editor callphoneEditor = callphonePerferences.edit();  
		        callphoneEditor.putBoolean("makecall", true);  
		        callphoneEditor.commit(); 
		        emergencybtn.setEnabled(true);
		        Toast.makeText(getBaseContext(), "緊急連絡人設定完成", Toast.LENGTH_LONG).show();
	        }else{
	        	SharedPreferences callphonePerferences = getSharedPreferences("callphonesetting",Activity.MODE_PRIVATE );  
		        SharedPreferences.Editor callphoneEditor = callphonePerferences.edit();  
		        callphoneEditor.putBoolean("makecall", false);  
		        emergencybtn.setEnabled(false);
		        callphoneEditor.commit(); 
		        Toast.makeText(getBaseContext(), "關閉撥打電話 ", Toast.LENGTH_LONG).show();
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
