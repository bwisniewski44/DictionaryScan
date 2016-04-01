package com.manolovindustries.dictionaryscan;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * FilterTask
 *
 * Asynchronous task to handle the filtering while the main program
 * allows for UI stuff.
 */
public class LoadTask extends AsyncTask<Void, Void, Void> {

    private WordPool dictionary;
    private MainActivity activity;
    private ProgressDialog dlg;

    private static final boolean TESTING = false;

    /**
     * CONSTRUCTOR
     *
     * @param activity activity in which this task occurs
     */
    public LoadTask(MainActivity activity) {
        activity.dictionary = new WordPool();
        dictionary = activity.dictionary;
        this.activity = activity;
    }

    /**
     * Pre-execution tasks involve the definition and display
     * of a progress dialog.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dlg = new ProgressDialog(activity);
        dlg.setMessage("Loading");
        dlg.setCancelable(false);

        dlg.show();
    }

    /**
     * Perform the filtering. Scan over possibilities and
     * put matches into result.
     *
     * @param params background task parameter array
     * @return data going to post-execution
     */
    @Override
    protected Void doInBackground(Void... params) {

        // Load the dictionary
        dictionary.clear();
        try {

            // Determine source list
            int resID;
            if (TESTING) {
                resID = R.raw.test;
            } else {
                resID = R.raw.dictionary;
            }

            // Load source
            InputStream iStream = activity.getResources().openRawResource(resID);
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            // While there is input to read...
            String line = br.readLine();
            while (line != null) {
                dictionary.add(line);
                line = br.readLine();
            }

            br.close();
            iStream.close();
        }
        catch (IOException e) {
            Log.d("LOADTASK", e.getMessage());
        }
        Log.d("DICTIONARY_SCAN", Integer.toString(dictionary.size()) + " words loaded.");

        return null;
    }

    /**
     * Progress bar is cancelled. UI is updated.
     * @param result results of main subroutine
     */
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        // Close dialog
        if (dlg.isShowing()) {
            try {
                dlg.dismiss();

            }
            catch (IllegalArgumentException e) {
                Log.d("LOAD TASK", "Dialog dismiss failure.");
                Log.d("LOAD TASK", e.getMessage());
            }
        }

        activity.setLengthBounds(1, activity.dictionary.maxLength());
    }
}
