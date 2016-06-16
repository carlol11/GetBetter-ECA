package com.geebeelicious.geebeelicious.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.database.DatabaseAdapter;
import com.geebeelicious.geebeelicious.fragments.ECAFragment;

import java.sql.SQLException;

/**
 * Created by Kate.
 * The MainActivity serves as the activity containing
 * the welcome screen and allows access to the Settings activity.
 */

public class MainActivity extends ActionBarActivity implements ECAFragment.OnFragmentInteractionListener {
    private ECAFragment ecaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startButton = (Button)findViewById(R.id.startButton);
        ImageView settingsButton = (ImageView)findViewById(R.id.settingsButton);

        integrateECA();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PatientListActivity.class);
                startActivity(intent);
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        DatabaseAdapter getBetterDb = new DatabaseAdapter(MainActivity.this);
        try {
            getBetterDb.createDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            finish(); //exit app if database creation fails
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Welcome message
        ecaFragment.sendToECAToSpeak("Hello, I'm Geebee! Are you ready?");
    }

    private void integrateECA() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ecaFragment = (ECAFragment) fragmentManager.findFragmentByTag(ECAFragment.class.getName());
        if(ecaFragment == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            ecaFragment = new ECAFragment();
            transaction.add(R.id.placeholderECA, ecaFragment, ECAFragment.class.getName());
            transaction.commit();

        }
    }

    //TODO: dito yung communication from ecafragment
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
