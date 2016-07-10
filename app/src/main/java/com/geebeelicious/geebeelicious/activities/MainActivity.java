package com.geebeelicious.geebeelicious.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.database.DatabaseAdapter;
import com.geebeelicious.geebeelicious.interfaces.ECAActivity;

import java.sql.SQLException;

/**
 * Created by Kate.
 * The MainActivity serves as the activity containing
 * the welcome screen and allows access to the Settings activity.
 */

public class MainActivity extends ECAActivity{
    private boolean hasSpoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startButton = (Button)findViewById(R.id.startButton);
        ImageView settingsButton = (ImageView)findViewById(R.id.settingsButton);

        integrateECA();

        if(savedInstanceState == null){
            hasSpoken = false;

            DatabaseAdapter getBetterDb = new DatabaseAdapter(MainActivity.this);
            try {
                getBetterDb.createDatabase();
            } catch (SQLException e) {
                e.printStackTrace();
                finish(); //exit app if database creation fails
            }
        } else {
            hasSpoken = savedInstanceState.getBoolean("hasSpoken");
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PatientListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            if(!hasSpoken){
                //Welcome message
                ecaFragment.sendToECAToSPeak(R.string.app_intro);
                hasSpoken = true;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("hasSpoken", hasSpoken);
    }
}
