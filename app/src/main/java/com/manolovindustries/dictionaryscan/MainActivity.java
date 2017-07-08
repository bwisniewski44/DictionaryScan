package com.manolovindustries.dictionaryscan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity { // instead of action bar activity

    // Logic-side vars
    public WordPool dictionary;

    // Edit text selections
    private static final int REGEX_FIELD = 0;
    private static final int JUMBLE_FIELD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load the dictionary
        LoadTask loadThread = new LoadTask(this);
        loadThread.execute();

        // Perform RegEx filter
        Button findButton = (Button)findViewById(R.id.regex_button);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                collapseKeyboard();

                // Build a regex filter
                RegexFilter master = new RegexFilter();
                performFilter(master, getInput(REGEX_FIELD));
            }
        });

        // Perform jumble filter
        Button jumbleButton = (Button)findViewById(R.id.jumble_button);
        jumbleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                collapseKeyboard();

                Spinner behaviorSelect = (Spinner)findViewById(R.id.jumble_behavior_spinner);
                JumbleFilter filter = new JumbleFilter(behaviorSelect.getSelectedItemPosition());
                performFilter(filter, getInput(JUMBLE_FIELD));
            }
        });

        // Perform substitution filter
        Button substitutionButton = (Button)findViewById(R.id.substitution_button);
        substitutionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                collapseKeyboard();

                // Extract text from field
                EditText et = (EditText)findViewById(R.id.substitution_filter_field);
                String filterDefinition = et.getText().toString();
                Filter substitutionFilter = new ExclusiveSubstitutionFilter();
                Log.d("MAIN", "Preparing to pass '" + filterDefinition + "' into filter activity.");
                performFilter(substitutionFilter, filterDefinition);
            }
        });

        // Clear button
        Button clearButton = (Button)findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                collapseKeyboard();
                clearList();

                Log.d("MAIN", "Source list contains word count =" + Integer.toString(dictionary.size()));
                LoadTask loadTask = new LoadTask(MainActivity.this);
                loadTask.execute();
            }
        });

        // Initialize spinner
        Spinner jumbleBehavior = (Spinner)findViewById(R.id.jumble_behavior_spinner);
        jumbleBehavior.setSelection(JumbleFilter.DEFAULT_MODE);
        ArrayAdapter ada = ArrayAdapter.createFromResource(this, R.array.jumble_behavior_options, android.R.layout.simple_spinner_dropdown_item);
        jumbleBehavior.setAdapter(ada);

        // Update button
        Button refreshButton = (Button)findViewById(R.id.show_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printMatchList();
            }
        });

        // Filter button
        Button lengthFilterButton = (Button)findViewById(R.id.length_filter_button);
        lengthFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the bounds set in the spinners
                Spinner minSpinner = (Spinner)findViewById(R.id.minimum_spinner);
                Spinner maxSpinner = (Spinner)findViewById(R.id.maximum_spinner);
                int min = minSpinner.getSelectedItemPosition();
                int max = maxSpinner.getSelectedItemPosition();

                // If there is a maximum, but it's less than the minimum...
                boolean maxIsSet = (max > 0);
                if (maxIsSet && (max < min)) {  // ... show an alert dialog and cancel
                    AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                    dlg.setTitle("Incompatible Bounds");
                    dlg.setMessage("The minimum length is greater than the maximum length."
                            + " Nothing may pass through this filter.");
                    dlg.setNeutralButton("Cancel", null);
                    dlg.show();
                }

                // otherwise, perform filter
                else {
                    Filter master = new LengthFilter(min, max);
                    performFilter(master, master.toString());
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    /**
     * Format and pull input from field.
     * @param field constant flag identifying edit text selection
     * @return formatted input
     */
    private String getInput(int field) {
        int fieldID;
        switch (field) {
            case (JUMBLE_FIELD) :
                fieldID = R.id.jumble_field;
                break;
            default :
                fieldID = R.id.regex_field;
                break;
        }
        EditText et = (EditText)findViewById(fieldID);
        String result = et.getText().toString().toUpperCase();
        et.setText(result);
        return result;
    }

    /**
     * Given a master filter from which others may be spawned,
     * this method begins the asynchronous filtering activity.
     * @param master filter object responsible for spawning others
     */
    private void performFilter(Filter master, String definition) {

        // Define the filtering activity parameters
        FilterParams params = new FilterParams();
        params.filter = master;
        params.definition = definition;
        params.activity = MainActivity.this;

        // Perform the asynchronous filter activity
        FilterTask ft = new FilterTask(params);
        ft.execute();
    }

    /**
     * Hides any soft keyboard otherwise existing
     * on the screen.
     */
    private void collapseKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Clears UI-side list and checks for display
     * conditions before proceeding to initiate
     * display task.
     */
    public void beginMatchListPrint() {

        clearList();

        if (dictionary.size() == 0) {
            Toast.makeText(this, "NO MATCHES", Toast.LENGTH_SHORT).show();
        }

        final int LIMIT = 25;
        if (dictionary.size() <= LIMIT) {
            printMatchList();
        }
    }

    /**
     * Initiates the asynchronous task responsible for
     * populating the UI-side match list with the entries
     * contained in the logic-side match list.
     */
    public void printMatchList() {

        LoadDisplayTask ldt = new LoadDisplayTask(this);
        ldt.execute();
    }

    /**
     * Clears the UI-side match list.
     */
    public void clearList() {
        ListView lv = (ListView)findViewById(R.id.match_list);
        List<String> list = new ArrayList<>();
        ListAdapter ad = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(ad);
    }

    public void setLengthBounds(int min, int max) {
        Spinner minSpin = (Spinner)findViewById(R.id.minimum_spinner);
        Spinner maxSpin = (Spinner)findViewById(R.id.maximum_spinner);

        int range = (max - min + 1);
        String[] options = new String[range + 1];
        for (int i=0; i<range; i++) {
            options[i + 1] = Integer.toString(min + i);
        }
        options[0] = "n/a";

        SpinnerAdapter sa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        minSpin.setAdapter(sa);
        maxSpin.setAdapter(sa);

        minSpin.setSelection(0);
        maxSpin.setSelection(0);
    }
}
