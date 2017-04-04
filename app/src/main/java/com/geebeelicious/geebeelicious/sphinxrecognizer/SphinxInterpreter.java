package com.geebeelicious.geebeelicious.sphinxrecognizer;

/**
 * Created by NeilJustin on 2/3/2017.
 */

public interface SphinxInterpreter {

    /**
     * This method is called when the recognizer obtains a result from the search
     * @param result the recognized word from the recognizer search
     */
    public void resultReceived(String result);
}