package com.geebeelicious.geebeelicious.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.geebeelicious.geebeelicious.R;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import edu.usc.ict.vhmobile.VHMobileLib;
import edu.usc.ict.vhmobile.VHMobileMain;
import edu.usc.ict.vhmobile.VHMobileSurfaceView;

/**
 * Created by MG.
 * The ECAFragment serves as the fragment that contains the
 * ECA. This fragment uses VHMobile library to implement the ECA
 */
public class ECAFragment extends Fragment {

    private static final String TAG = "ECAFragment";


    private OnFragmentInteractionListener mListener;

    protected VHMobileMain vhmain = null;
    protected VHMobileSurfaceView _VHview = null;

    public ECAFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_eca, container, false);

        //ECA integration
        VHMobileMain.setupVHMobile();


        Log.d(TAG,  "The onCreate() event");

        vhmain = new VHMobileMain((Activity)mListener);
        vhmain.init();

        //TODO: check if tanggalin itong internet address crap. kung gagana pa rin
        // seed the ip addresses of the mobile device
        // can't find way to do this in JNI, so send it through Java

        String ipAddress = new String();
        boolean done = false;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                if (done)
                    break;
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                     enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    Log.d("SB", "Found internet address" + inetAddress.getHostAddress().toString());
                    if (!inetAddress.isLoopbackAddress())
                    {
                        ipAddress = inetAddress.getHostAddress().toString();
                        if (ipAddress.indexOf(".") >= 0)
                        {
                            Log.d("SB", "Found non-loopback address" + ipAddress);

                            done = true;
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("SB", "Problem getting IP addresses.");
        }

        Log.d(TAG, "Setting debugger hostname to " + ipAddress);
        VHMobileLib.executeSB("scene.getDebuggerServer().setStringAttribute(\"hostname\", \"" + ipAddress + "\")");


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart()
    {
        Log.d("SB",  "The onStart() event");
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        /*
         * The activity must call the GL surface view's
         * onResume() on activity onResume().
         */
        if (_VHview != null)
            _VHview.onResume();
        Log.d("SB", "The onResume() event");
    }

    @Override
    public void onPause() {
        super.onPause();
        /*
         * The activity must call the GL surface view's
         * onResume() on activity onResume().
         */
        if (_VHview != null)
            _VHview.onPause();
        Log.d("SB", "The onPause() event");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /** Called when the activity is no longer visible. */
    @Override
    public void onStop() {
        super.onStop();
        Log.d("SB", "The onStop() event");
    }

    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SB", "The onDestroy() event");
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void sendToECAToSpeak(String sentence){
        VHMobileLib.executeSB("saySomething(characterName, \""+ sentence+"\")");
    }
}
