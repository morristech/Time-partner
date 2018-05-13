package me.shouheng.timepartner.activities.zone;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import me.shouheng.timepartner.R;

public class ZoneDashboard extends AppCompatActivity {
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_zone_layout);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.zone_title));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
