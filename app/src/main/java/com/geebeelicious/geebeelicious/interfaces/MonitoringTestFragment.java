package com.geebeelicious.geebeelicious.interfaces;

import android.support.v4.app.Fragment;

/**
 * Created by mgmalana.
 * MonitoringTestFragment is an abstract class
 * used in MonitoringMainActivity
 */
public abstract class MonitoringTestFragment extends Fragment{
    protected int introStringResource;
    protected int endStringResource;

    public int getIntroStringResource() {
        return introStringResource;
    }

    public int getEndStringResource() {
        return endStringResource;
    }

    public void setIntroStringResource(int introStringResource) {
        this.introStringResource = introStringResource;
    }

    public void setEndStringResource(int endStringResource) {
        this.endStringResource = endStringResource;
    }
}
