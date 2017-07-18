package com.csie_chicago.csie_chicago;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.TextView;



public class AbstractBasePreferencesActivity extends PreferenceActivity {
    private TextView txtActionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        txtActionBarTitle = getTxtActionBarTitle();
        setTitle(getTitle());
    }









    public Activity getActivity() {
        return this;
    }

    public Context getContext() {
        return this;
    }

    private TextView getTxtActionBarTitle() {
        return (txtActionBarTitle == null) ? (TextView) findViewById(R.id.txtActionBarTitle)
                : txtActionBarTitle;
    }
}