package com.geebeelicious.geebeelicious.interfaces;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by mgmalana.
 * MonitoringTestFragment is an abstract class
 * used in MonitoringMainActivity
 */
public abstract class MonitoringTestFragment extends Fragment{
    protected int intro;
    protected int endStringResource;
    protected int endTime;
    protected boolean isEndEmotionHappy; //true happy, false concern
    protected boolean hasEarlyInstruction;

    protected View view;

    public int getIntro() {
        return intro;
    }

    public int getEndStringResource() {
        return endStringResource;
    }

    public int getEndTime() {
        return endTime;
    }

    public boolean isEndEmotionHappy() {
        return isEndEmotionHappy;
    }

    public boolean hasEarlyInstruction() {
        return hasEarlyInstruction;
    }

    public void hideFragmentMainView(){
        view.setVisibility(View.GONE);
    }
}
