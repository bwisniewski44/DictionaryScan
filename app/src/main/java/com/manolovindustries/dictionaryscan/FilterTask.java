package com.manolovindustries.dictionaryscan;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.Semaphore;

/**
 * FilterTask
 *
 * Asynchronous task to handle the filtering while the main program
 * allows for UI stuff.
 */
public class FilterTask extends AsyncTask<Void, Void, Void> {

    private WordPool possibilities;
    private MainActivity activity;
    private Filter master;
    private String definition;
    private long begin, end;

    private int oldCt;

    private ProgressDialog dlg;

    /**
     * CONSTRUCTOR
     *
     * @param params activity in which this task occurs
     */
    public FilterTask(FilterParams params) {

        this.master = params.filter;
        this.definition = params.definition;
        this.activity = params.activity;

        possibilities = this.activity.dictionary;
        oldCt = possibilities.size();
        begin = System.currentTimeMillis();
    }

    /**
     * Pre-execution tasks involve the definition and display
     * of a progress dialog.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Log.d("PRE-EX", "Defining dialog.");
        dlg = new ProgressDialog(activity);
        dlg.setMessage("Scanning");
        dlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dlg.setMax(possibilities.size());
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

        // Determine thread count
        final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
        Log.d("THREAD TASK", Integer.toString(THREAD_COUNT) + " threads will be spawned.");
        final Thread[] scanners = new Thread[THREAD_COUNT];

        final Semaphore readLock = new Semaphore(1, true);
        final Semaphore writeLock = new Semaphore(1, true);

        final WordPool results = new WordPool();

        begin = System.currentTimeMillis();

        // For each thread...
        for (int i=0; i < THREAD_COUNT; i++) {

            // Determine its position
            //final int THREAD_INDEX = i;

            // Define its subroutine
            scanners[i] = new Thread() {
                @Override
                public void run() {

                    // Generate a filter
                    Filter filter = master.spawn(definition);

                    // While this thread is processing items...
                    while (true) {

                        // Try to pull a word
                        String word;
                        try {
                            readLock.acquire();                 // acquire a read lock
                            if (possibilities.size() > 0) {     // read the state of the possibility pool
                                word = possibilities.remove();  // pull a word if it exists
                            }
                            else {                              // if no more possibilities, release and break
                                readLock.release();             // read lock must be unconditional
                                break;
                            }
                            readLock.release();
                        }
                        catch (InterruptedException e) {
                            Log.d("Thread ABORT", e.getMessage());
                            break;
                        }

                        // If the word passes the filter...
                        if (filter.pass(word)) {

                            // ... add it to the result
                            try {
                                writeLock.acquire();
                                results.add(word);
                                writeLock.release();
                            }
                            catch (InterruptedException e) {
                                Log.d("Thread ABORT", e.getMessage());
                            }
                        }
                        dlg.incrementProgressBy(1);
                    }
                }
            };
            scanners[i].start();
        }

        // Wait for all threads to close
        for (int i=0; i<THREAD_COUNT; i++) {
            try {
                scanners[i].join();

            }
            catch (InterruptedException e) {
                Log.d("THREAD ERROR", e.getMessage());
            }
        }

        end = System.currentTimeMillis();

        activity.dictionary = results;
        Log.d("FILTER", Integer.toString(results.size()) + " matches found.");

        return null;
    }

    /**
     * Progress bar is cancelled. UI is updated.
     * @param result results of main subroutine
     */
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        Log.d("POST-EX", "Updating UI and dismissing dlg.");

        // Close dialog
        if (dlg.isShowing()) {
            dlg.dismiss();
        }

        // Show filter effects
        int remain = activity.dictionary.size();
        int removed = oldCt - remain;

        Log.d("FILTER", Integer.toString(activity.dictionary.size()) + " matches transferred to list.");
        activity.setLengthBounds(1, activity.dictionary.maxLength());
        activity.beginMatchListPrint();

        Toast.makeText(activity, "Task concluded in " + Long.toString((end-begin)/1000) + " seconds.", Toast.LENGTH_SHORT).show();
    }
}
