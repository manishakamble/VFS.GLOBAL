package com.vfs.fingerprint.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.vfs.fingerprint.R;

public class AdminPortalActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cdViewRecords;
    private CardView cdViewSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_portal);

        cdViewRecords = (CardView) findViewById(R.id.card_view);
        cdViewSetting = (CardView) findViewById(R.id.card_view_setting);
        cdViewRecords.setOnClickListener(this);
        cdViewSetting.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.card_view) {
            /*Intent intent = new Intent(AdminPortalActivity.this, AdminScreenActivity.class);
            startActivity(intent);*/
        } else if (v.getId() == R.id.card_view_setting) {
            Intent intent = new Intent(AdminPortalActivity.this, SettingsDynamic.class);
            startActivity(intent);

        }
    }
}
