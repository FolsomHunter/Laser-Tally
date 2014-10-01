/******************************************************************************
 * Title: CreateJobActivity.java
 * Author: Hunter Schoonover
 * Date: 09/15/14
 *
 * Purpose:
 *
 * This class is used as an activity to display a user interface that allows
 * users to edit job info to create a job.
 *
 */

//-----------------------------------------------------------------------------

package com.yaboosh.ybtech.lasertally;

//-----------------------------------------------------------------------------

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
// class CreateJobActivity
//

public class CreateJobActivity extends Activity {

    public static final String TAG = "JobInfoActivity";

    private View decorView;
    private int uiOptions;

    public static final String JOB_INFO_INCLUDED = "JOB_INFO_INCLUDED";
    public static final String COMPANY_NAME_KEY = "COMPANY_NAME_KEY";
    public static final String DIAMETER_KEY = "DIAMETER_KEY";
    public static final String FACILITY_KEY = "FACILITY_KEY";
    public static final String GRADE_KEY = "GRADE_KEY";
    public static final String JOB_KEY =  "JOB_KEY";
    public static final String MAKEUP_ADJUSTMENT_KEY = "PROTECTOR_MAKE_UP_ADJUSTMENT_KEY";
    public static final String RACK_KEY = "RACK_KEY";
    public static final String RANGE_KEY = "RANGE_KEY";
    public static final String RIG_KEY = "RIG_KEY";
    public static final String WALL_KEY = "WALL_KEY";

    ArrayList<String> fileLines = new ArrayList<String>();

    private String companyName;
    private String diameter;
    private String facility;
    private String grade;
    private String job;
    private String makeupAdjustment;
    private String rack;
    private String range;
    private String rig;
    private String wall;

    //-----------------------------------------------------------------------------
    // CreateJobActivity::CreateJobActivity (constructor)
    //

    public CreateJobActivity() {

        super();

    }//end of CreateJobActivity::CreateJobActivity (constructor)
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // CreateJobActivity::onCreate
    //
    // Automatically called when the activity is created.
    // All functions that must be done upon creation should be called here.
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d(TAG, "Inside of JobInfoActivity onCreate");

        setContentView(R.layout.activity_create_job);

        this.setFinishOnTouchOutside(false);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        decorView = getWindow().getDecorView();

        uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

        createUiChangeListener();

        ((TextView)findViewById(R.id.editTextJob)).addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable pE) {
                handleEditTextJobTextChanged(pE.length());
            }

            public void beforeTextChanged(CharSequence pS, int pStart, int pCount, int pAfter) {
            }

            public void onTextChanged(CharSequence pS, int pStart, int pBefore, int pCount) {
            }
        });

    }//end of CreateJobActivity::onCreate
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // CreateJobActivity::onDestroy
    //
    // Automatically called when the activity is destroyed.
    // All functions that must be done upon destruction should be called here.
    //

    @Override
    protected void onDestroy()
    {

        Log.d(TAG, "Inside of JobInfoActivity onDestroy");

        super.onDestroy();

    }//end of CreateJobActivity::onDestroy
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // CreateJobActivity::onResume
    //
    // Automatically called when the activity is paused when it does not have
    // user's focus but it still partially visible.
    // All functions that must be done upon instantiation should be called here.
    //

    @Override
    protected void onResume() {

        super.onResume();

        Log.d(TAG, "Inside of JobInfoActivity onResume");

        decorView.setSystemUiVisibility(uiOptions);

    }//end of CreateJobActivity::onResume
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // CreateJobActivity::onPause
    //
    // Automatically called when the activity is paused when it does not have
    // user's focus but it still partially visible.
    // All functions that must be done upon instantiation should be called here.
    //

    @Override
    protected void onPause() {

        super.onPause();

        Log.d(TAG, "Inside of JobInfoActivity onPause");

    }//end of CreateJobActivity::onPause
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // CreateJobActivity::createUiChangeListener
    //
    // Listens for visibility changes in the ui.
    //
    // If the system bars are visible, the system visibility is set to the uiOptions.
    //
    //

    private void createUiChangeListener() {

        decorView.setOnSystemUiVisibilityChangeListener (
                new View.OnSystemUiVisibilityChangeListener() {

                    @Override
                    public void onSystemUiVisibilityChange(int pVisibility) {

                        if ((pVisibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            decorView.setSystemUiVisibility(uiOptions);
                        }

                    }

                });

    }//end of CreateJobActivity::createUiChangeListener
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // CreateJobActivity::exitActivityByCancel
    //
    // Used when the user closes the activity using the cancel or red x button.
    // Sets the result to canceled and finishes the activity.
    //

    private void exitActivityByCancel() {

        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();

    }//end of CreateJobActivity::exitActivityByCancel
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // CreateJobActivity::exitActivityByOk
    //
    // Used when the user closes the activity using the ok button.
    // Gets the job info and puts it into a file and into the intent extras, sets
    // the result to ok, and finishes the activity.
    //
    //

    private void exitActivityByOk() {

        getAndStoreJobInfoFromUserInput();

        saveInformationToFile();

        Intent intent = new Intent(this, JobDisplayActivity.class);

        intent.putExtra(JOB_INFO_INCLUDED, true);
        intent.putExtra(COMPANY_NAME_KEY, companyName);
        intent.putExtra(DIAMETER_KEY, diameter);
        intent.putExtra(FACILITY_KEY, facility);
        intent.putExtra(GRADE_KEY,  grade);
        intent.putExtra(JOB_KEY, job);
        intent.putExtra(MAKEUP_ADJUSTMENT_KEY, makeupAdjustment);
        intent.putExtra(RACK_KEY, rack);
        intent.putExtra(RANGE_KEY, range);
        intent.putExtra(RIG_KEY, rig);
        intent.putExtra(WALL_KEY, wall);

        startActivity(intent);
        finish();

    }//end of CreateJobActivity::exitActivityByOk
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // CreateJobActivity::getAndStoreJobInfoFromUserInput
    //
    // Gets and stores the job info into variables by retrieving the values
    // entered by the user.
    //

    private void getAndStoreJobInfoFromUserInput() {

        companyName = ((TextView) findViewById(R.id.editTextCompanyName)).getText().toString();
        diameter = ((TextView) findViewById(R.id.editTextDiameter)).getText().toString();
        facility = ((TextView) findViewById(R.id.editTextFacility)).getText().toString();
        grade = ((TextView) findViewById(R.id.editTextGrade)).getText().toString();
        job = ((TextView) findViewById(R.id.editTextJob)).getText().toString();
        makeupAdjustment = ((TextView)
                        findViewById(R.id.editTextProtectorMakeupAdjustment)).getText().toString();
        rack = ((TextView) findViewById(R.id.editTextRack)).getText().toString();
        range = ((TextView) findViewById(R.id.editTextRange)).getText().toString();
        rig = ((TextView) findViewById(R.id.editTextRig)).getText().toString();
        wall = ((TextView) findViewById(R.id.editTextWall)).getText().toString();

    }//end of CreateJobActivity::getAndStoreJobInfoFromUserInput
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // CreateJobActivity::handleCancelButtonPressed
    //
    // Exits the activity by calling exitActivityByCancel().
    //

    public void handleCancelButtonPressed(View pView) {

        exitActivityByCancel();

    }//end of CreateJobActivity::handleCancelButtonPressed
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // CreateJobActivity::handleEditTextJobTextChanged
    //
    // Checks to see if the passed in length is more than 0. If it is, then the
    // ok button is enabled.
    //
    // Called when the text in the EditText used for the Job name is changed.
    //

    private void handleEditTextJobTextChanged(int pLength) {

        Boolean bool = false;

        if (pLength > 0) { bool = true; }

        Button okButton = (Button) findViewById(R.id.createJobOkButton);

        okButton.setEnabled(bool);

        if (bool) {
            okButton.setTextAppearance(getApplicationContext(), R.style.whiteStyledButton);
        } else {
            okButton.setTextAppearance(getApplicationContext(), R.style.disabledStyledButton);
        }

    }//end of CreateJobActivity::handleEditTextJobTextChanged
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // CreateJobActivity::handleOkButtonPressed
    //
    // Exits the activity by calling exitActivityByOk().
    //

    public void handleOkButtonPressed(View pView) {

        exitActivityByOk();

    }//end of CreateJobActivity::handleOkButtonPressed
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // CreateJobActivity::handleRedXButtonPressed
    //
    // Exits the activity by calling exitActivityByCancel().
    //

    public void handleRedXButtonPressed(View pView) {

        exitActivityByCancel();

    }//end of CreateJobActivity::handleRedXButtonPressed
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // CreateJobActivity::saveInformationToFile
    //
    // Stores the job info in a file.
    //

    private void saveInformationToFile() {

        // Retrieve/Create directory into internal memory;
        File jobsDir = getDir("jobsDir", Context.MODE_PRIVATE);

        // Retrieve/Create sub-directory thisJobDir
        File thisJobDir = new File(jobsDir, job);
        if (!thisJobDir.exists()) { Boolean success = thisJobDir.mkdir(); }

        // Get a file jobInfoTextFile within the dir thisJobDir.
        File jobInfoTextFile = new File(thisJobDir, "jobInfo.txt");
        try {
            if (!jobInfoTextFile.exists()) {
                Boolean success = jobInfoTextFile.createNewFile();
            }
        } catch (Exception e) {}

        // Use a PrintWriter to write to the file
        try {
            PrintWriter writer = new PrintWriter(jobInfoTextFile, "UTF-8");

            writer.println("Company Name=" + companyName);
            writer.println("Diameter=" + diameter);
            writer.println("Facility=" + facility);
            writer.println("Grade=" + grade);
            writer.println("Job=" + job);
            writer.println("Makeup Adjustment=" + makeupAdjustment);
            writer.println("Rack=" + rack);
            writer.println("Range=" + range);
            writer.println("Rig=" + rig);
            writer.println("Wall=" + wall);

            writer.close();
        } catch (Exception e) {
           Log.d(TAG, "Writing failed");
        }

    }//end of CreateJobActivity::saveInformationToFile
    //-----------------------------------------------------------------------------

}//end of class CreateJobActivity
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------