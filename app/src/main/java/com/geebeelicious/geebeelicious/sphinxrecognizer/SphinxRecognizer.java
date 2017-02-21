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

    /* Singleton attribute */
    private static SphinxRecognizer instance;

    /* Recognizer attributes */
    private boolean isReady;
    private SpeechRecognizer recognizer;
    private Context context;

    private ArrayList<SphinxInterpreter> interpreters;

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

    public static SphinxRecognizer getInstance(Context c){
        if(instance == null)
            instance = new SphinxRecognizer(c);

        return instance;
    }

    public static SphinxRecognizer getInstance(){
        if(instance != null)
            return instance;
        else
            return null;
    }

    public void addInterpreter(SphinxInterpreter interp){
        interpreters.add(interp);
    }

    public void clearInterpreters(){
        interpreters.clear();
    }

    private void notifyInterpreters(String result){
        for(int i=0; i<interpreters.size(); i++){
            interpreters.get(i).resultReceived(result);
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


        // Create grammar-based search for selection between demos
        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);

        // Create keyword search for binary answers (e.g. yes, no)
        File binAnswer = new File(assetsDir, "answer.kws");
        recognizer.addGrammarSearch(BINANSWER_SEARCH, binAnswer);

        // Phonetic search
        File phoneticModel = new File(assetsDir, "en-phone.dmp");
        recognizer.addAllphoneSearch(PHONE_SEARCH, phoneticModel);

        // Dictionary search
    }


    public void startSearch(String searchName) {
        recognizer.stop();
        Log.d(TAG,"startsearch1");
        recognizer.startListening(searchName);
        Log.d(TAG,"startsearch2");
    }

    public void startSearch(String searchName, int duration) {
        recognizer.stop();
        recognizer.startListening(searchName, duration);
    }

    public void stopRecognizer(){
        recognizer.stop();
    }

    public void closeRecognizer(){
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    public boolean isReady(){
        return isReady;
    }

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

        if(recognizer.getSearchName().equals(SphinxRecognizer.BINANSWER_SEARCH)) {
            text = text.trim();
            text = text.split(" ")[0];
        }

        Log.d(TAG,"partialResult: "+text);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onError(Exception e) {
        try {
            throw e;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onTimeout() {
        recognizer.stop();
    }
}
