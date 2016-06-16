package com.geebeelicious.geebeelicious.interfaces;

import com.geebeelicious.geebeelicious.models.monitoring.Record;

/**
 * Created by mgmalana on 15/05/2016.
 * This interface must be implemented in order to use the monitoring fragments.
 * It was created to make sure that the methods in this interface are overrided
 * by any activity or fragments that wants to interact with the fragments.
 * If not implemented, the fragment will throw an error;
 */
public interface OnMonitoringFragmentInteractionListener {
    public Record getRecord();
    public void setInstructions(String instructions);
    public void setResults(String results);
    public void doneFragment();
    public int getIntResults(String result);
}
