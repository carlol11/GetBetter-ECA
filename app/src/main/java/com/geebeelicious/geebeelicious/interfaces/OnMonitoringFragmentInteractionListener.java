package com.geebeelicious.geebeelicious.interfaces;

import com.geebeelicious.geebeelicious.models.monitoring.Record;

/**
 * This interface must be implemented in order to use the monitoring fragments.
 * It was created to make sure that the methods in this interface are overrided
 * by any activity or fragments that wants to interact with the fragments.
 * If not implemented, the fragment will throw an error;
 *
 * @author Mary Grace Malana
 * @since 15/05/2016

 */
public interface OnMonitoringFragmentInteractionListener {

    /**
     * Gets the record of the patient
     * @return record of patient
     */
    public Record getRecord();

    /**
     * Sets the instruction of the monitoring test
     * @param instructions new instruction to be set
     */
    public void setInstructions(String instructions);

    public void setInstructionsText(String instructions);

    /**
     * Sets the instruction of the monitoring test
     * @param resID string resource of new instruction to be set
     */
    public void setInstructions(int resID);

    /**
     * Sets the results of the monitoring test
     * @param results new instruction to be set
     */
    public void setResults(String results);

    /**
     * Called when the monitoring is done.
     */
    public void doneFragment();

    /**
     * Get the corresponding integer of the string result.
     * @param result Monitoring test result.
     * @return corresponding integer of the {@code result}
     */
    public int getIntResults(String result);

    /**
     * Compute the age of the patient by
     * using the {@link com.geebeelicious.geebeelicious.models.consultation.Patient#birthday}
     * @return age of patient.
     */
    public int getAge();

    /**
     * Get the patient's gender by getting
     * {@link com.geebeelicious.geebeelicious.models.consultation.Patient#gender}
     * @return true if patient is female, false if male.
     */
    public boolean isGirl();

    /**
     * Called by fragments who has an {@link MonitoringTestFragment#hasEarlyInstruction()}.
     * This is called when early instruction should be shown.
     */
    public void showTransitionTextLayout();

    /**
     * Appends the {@code instructions} to the existing instruction for the test.
     * @param instructions instructions to be added.
     */
    public void appendTransitionIntructions(String instructions);
}
