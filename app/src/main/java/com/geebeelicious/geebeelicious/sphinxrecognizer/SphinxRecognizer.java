package com.geebeelicious.geebeelicious.sphinxrecognizer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

/**
 * Created by NeilJustin on 1/31/2017.
 */

public class SphinxRecognizer implements RecognitionListener {

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    
    /* for logging purposes */
    public static final String TAG = "SphinxRecognizer";

    /* Named searches allow to quickly reconfigure the decoder */
    public static final String BINANSWER_SEARCH = "binary";
    public static final String PHONE_SEARCH = "phones";
    public static final String MENU_SEARCH = "menu";
    public static final String SHAPE_SEARCH = "shape";

    /* Singleton attribute */
    private static SphinxRecognizer instance;

    /* Recognizer attributes */
    private boolean isReady;
    private SpeechRecognizer recognizer;
    private Context context;

    /**
     * the observers(listeners) of the recognizer
     */
    private ArrayList<SphinxInterpreter> interpreters;

    /**
     * Constructor for the recognizer. Only called by getInstance method
     * @param context - the context of the mobile device
     */
    private SphinxRecognizer(Context context){

        // Check if user has given permission to record audio
        /*
        int permissionCheck = ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }*/

        interpreters = new ArrayList<>();

        setContext(context);
        runRecognizerSetup();
    }


    /**
     * returns an instance of SphinxRecognizer. Use this when instantiating the recognizer for the first time.
     * @param c - the context of the mobile app
     * @return returns the instance of the SphinxRecognizer
     */
    public static SphinxRecognizer getInstance(Context c){
        if(instance == null)
            instance = new SphinxRecognizer(c);

        return instance;
    }

    /**
     * returns an instance of SphinxRecognizer. Use this when instantiating the recognizer.
     * @return returns the instance of the SphinxRecognizer
     */
    public static SphinxRecognizer getInstance(){
        if(instance != null)
            return instance;
        else
            return null;
    }

    /**
     * adds an interpreter to the list of observers which are notified everytime a result is received.
     * @param interp the interpreter to be added
     */
    public void addInterpreter(SphinxInterpreter interp){
        interpreters.add(interp);
    }

    /**
     * removes all interpreters from the list of observers
     */
    public void clearInterpreters(){
        interpreters.clear();
    }

    /**
     * Notifies the interpreters in the list that a result is received.
     * @param result the result that is recognized by the recognizer
     */
    private void notifyInterpreters(String result){
        if(!interpreters.isEmpty()) {
            for (int i = 0; i < interpreters.size(); i++) {
                interpreters.get(i).resultReceived(result);
            }
        }
    }

    public void setContext(Context c){
        context = c;
    }

    private void runRecognizerSetup() {
        isReady = false;
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            ProgressDialog dialog = ProgressDialog.show(context, "Loading Recognizer", "Please wait...", true);
            @Override
            protected Exception doInBackground(Void... params) {

                try {

                    Assets assets = new Assets(context);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    e.printStackTrace();
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    try {
                        throw new Exception("Recognizer failed to initialize!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    isReady = true;
                }
                dialog.dismiss();
            }
        }.execute();
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "cmusphinx-en-us-ptm-5.2"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .getRecognizer();

        recognizer.addListener(this);

        /** In your application you might not need to add all those searches.
         * They are added here for demonstration. You can leave just one.
         */

        // Create keyword search for binary answers (e.g. yes, no)
        File binAnswer = new File(assetsDir, "answer_kws.txt");
        recognizer.addKeywordSearch(BINANSWER_SEARCH, binAnswer);

        //TODO: Shape search
        File shapekws = new File(assetsDir,"shapes_kws.txt");
        recognizer.addKeywordSearch(SHAPE_SEARCH,shapekws);
    }

    /**
     * Starts the speech recognition with the specified search mode
     * @param searchName - the name of the search mode to be used
     */
    public void startSearch(String searchName) {
        recognizer.stop();
        recognizer.startListening(searchName);
    }

    /**
     * Starts the speech recognition with the specified search mode and duration
     * @param searchName - the name of the search mode to be used
     * @param duration - the duration of how long (in milliseconds) the search would last
     */
    public void startSearch(String searchName, int duration) {
        recognizer.stop();
        recognizer.startListening(searchName, duration);
    }

    /**
     * stops the recognizer search
     */
    public void stopRecognizer(){
        recognizer.stop();
    }

    /**
     * closes the recognizer -- ONLY USE WHEN CLOSING THE APPLICATION
     */
    public void closeRecognizer(){
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    /**
     * checks if the recognizer is ready for searching
     * @return true if the recognizer is ready, otherwise returns false
     */
    public boolean isReady(){
        return isReady;
    }

    /**
     * called when the recognizer detects the beginning of speech
     */
    @Override
    public void onBeginningOfSpeech() {

    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {

    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();

        Log.d(TAG,"full-partialResult: "+text);

        if(recognizer.getSearchName().equals(SphinxRecognizer.BINANSWER_SEARCH) || recognizer.getSearchName().equals(SphinxRecognizer.SHAPE_SEARCH)) {
            text = text.trim();
            String textTokens[] = text.split(" ");
            if(textTokens[0].matches("semi|half"))
                text = textTokens[0].concat(" "+textTokens[1]);
            else
                text = textTokens[0];
        }

        Log.d(TAG,"partialResult: "+text);
        notifyInterpreters(text);
    }


    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        /*
        if (hypothesis != null) {
            Log.d(TAG,"RESULT");
            String text = hypothesis.getHypstr();
            text = text.trim();
            text = text.substring(text.lastIndexOf(' ') + 1);
            notifyInterpreters(text);
        }
        */
    }

    /**
     * called when an exception occurs while searching
     * @param e
     */
    @Override
    public void onError(Exception e) {
        try {
            throw e;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * called when the search duration is finished
     */
    @Override
    public void onTimeout() {
        recognizer.stop();
    }
}
