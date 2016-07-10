package com.geebeelicious.geebeelicious.interfaces;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by mgmalana.
 * MonitoringTestFragment is an abstract class
 * used in MonitoringMainActivity
 */
public abstract class MonitoringTestFragment extends Fragment{
    protected int introStringResource;
    protected int endStringResource;
    protected int introTime;
    protected int endTime;

    protected View view;

    public int getIntroStringResource() {
        return introStringResource;
    }

    public int getEndStringResource() {
        return endStringResource;
    }

    public int getIntroTime() {
        return introTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void hideFragmentMainView(){
        view.setVisibility(View.GONE);
    }
}
