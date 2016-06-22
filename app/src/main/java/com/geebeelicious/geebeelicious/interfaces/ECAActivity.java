package com.geebeelicious.geebeelicious.interfaces;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.fragments.ECAFragment;

/**
 * Created by mgmalana.
 * The ECAActivity serves as the abstract class for activities that uses the ECAFragment
 */
public abstract class ECAActivity extends ActionBarActivity implements ECAFragment.OnFragmentInteractionListener {
    protected ECAFragment ecaFragment;

    protected void integrateECA() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ecaFragment = (ECAFragment) fragmentManager.findFragmentByTag(ECAFragment.class.getName());
        if(ecaFragment == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            ecaFragment = new ECAFragment();
            transaction.add(R.id.placeholderECA, ecaFragment, ECAFragment.class.getName());
            transaction.commit();

        }
    }

    @Override
    public void onClickECAFragment() {

    }
}
