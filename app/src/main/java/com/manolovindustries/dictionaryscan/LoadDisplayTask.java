package com.manolovindustries.dictionaryscan;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

/**
 * FilterTask
 *
 * Asynchronous task to handle the filtering while the main program
 * allows for UI stuff.
 */
public class LoadDisplayTask extends AsyncTask<Void, Void, Void> {

    private WordPool words;
    private MainActivity activity;
    private ProgressDialog dlg;

    List<String> wordlist;

    private static final boolean TESTING = true;

    /**
     * CONSTRUCTOR
     *
     * @param activity activity in which this task occurs
     */
    public LoadDisplayTask(MainActivity activity) {
        words = activity.dictionary;
        this.activity = activity;

        wordlist = new ArrayList<>();
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
        dlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dlg.setMax(words.size());
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



        //List<String> temp = new ArrayList<>();
        for (int i=0; i<words.size(); i++) {
            String word = words.remove();
            wordlist.add(wordlist.size(), word);
            words.add(word);
            dlg.incrementProgressBy(1);
        }

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

        ListView lv = (ListView)activity.findViewById(R.id.match_list);
        ListAdapter la = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, wordlist);
        lv.setAdapter(la);
    }
}
