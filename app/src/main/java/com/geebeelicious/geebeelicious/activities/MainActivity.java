package com.geebeelicious.geebeelicious.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startButton = (Button)findViewById(R.id.startButton);
        ImageView settingsButton = (ImageView)findViewById(R.id.settingsButton);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        final Fragment ECAFragment = new ECAFragment();
        transaction.replace(R.id.placeholderECA, ECAFragment);
        transaction.commit();


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, PatientListActivity.class);
//                startActivity(intent);
                resizeFragment(ECAFragment, 200, 200);
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

    //TODO: dito yung communication from ecafragment
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void resizeFragment(Fragment f, int newWidth, int newHeight) {
        if (f != null) {
            View view = f.getView();
            FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(newWidth, newHeight);
            view.setLayoutParams(p);
            view.requestLayout();


        }
    }

}
