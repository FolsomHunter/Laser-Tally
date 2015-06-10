/******************************************************************************
* Title: StandardActivity.java
* Author: Hunter Schoonover
* Date: 06/07/15
*
* Purpose:
*
* This class is used intended to be a parent class for all activities.
*
* All activities should extend this class and make use of its functions and
* variables.
*
*/

//-----------------------------------------------------------------------------

package com.yaboosh.ybtech.lasertally;

//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
// class StandardActivity
//

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

public class StandardActivity extends Activity {

    public static String LOG_TAG = "StandardActivity";

    protected int layoutResID;

    private View decorView;
    private int uiOptions;

    protected JobInfo jobInfo;
    protected SharedSettings sharedSettings;

    //Used for the focus order
    //different children activities add different
    //focus elements
    protected ArrayList<View> focusArray = new ArrayList<View>();
    protected int startingIndexOfFocusArray = 0;
    protected View viewInFocus;

    //-----------------------------------------------------------------------------
    // StandardActivity::onCreate
    //
    // Automatically called when the activity is created.
    // All functions that must be done upon instantiation should be called here.
    //

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {

        super.onCreate(pSavedInstanceState);

        //DEBUG HSS
        Log.d(LOG_TAG, "Inside of onCreate");

        setContentView(layoutResID);

        setFinishOnTouchOutside(false);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        createUiChangeListener();

        // Check whether we're recreating a previously destroyed instance
        if (pSavedInstanceState != null) {
            restoreValuesFromSavedInstance(pSavedInstanceState);
            restoreActivitySpecificValuesFromSavedInstance(pSavedInstanceState);
        }
        else {
            useActivityStartUpValues();
            useActivitySpecificActivityStartUpValues();
        }

        performOnCreateActivitySpecificActions();

    }//end of StandardActivity::onCreate
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::onResume
    //
    // Automatically called when the activity is paused when it does not have
    // user's focus but it still partially visible.
    //
    // All functions that must be done upon activity resume should be called here.
    //

    @Override
    protected void onResume() {

        super.onResume();

        //DEBUG HSS
        Log.d(LOG_TAG, "Inside of onResume");

        decorView.setSystemUiVisibility(uiOptions);

        sharedSettings.setContext(this);

        performOnResumeActivitySpecificActions();

    }//end of StandardActivity::onResume
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::dispatchKeyEvent
    //
    // Overrides actions for certain key events. If we don't want to override
    // the action for a key event, then it is passed up to the parent class for
    // handling.
    //
    // Called to process key events. You can override this to intercept all key
    // events before they are dispatched to the window. Be sure to call this
    // implementation for key events that should be handled normally.
    //

    @Override
    public boolean dispatchKeyEvent(KeyEvent pEvent) {

        boolean actionUpEvent = false;
        if (pEvent.getAction() == KeyEvent.ACTION_UP) { actionUpEvent = true; }

        switch (pEvent.getKeyCode()) {

            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (actionUpEvent) { handleArrowDownKeyPressed(); }
                return true;

            case KeyEvent.KEYCODE_DPAD_UP:
                if (actionUpEvent) { handleArrowUpKeyPressed(); }
                return true;

            case KeyEvent.KEYCODE_ESCAPE:
                if (actionUpEvent) { handleEscapeKeyPressed(); }
                return true;

            case KeyEvent.KEYCODE_F1:
                if (actionUpEvent) { handleF1KeyPressed(); }
                return true;

            case KeyEvent.KEYCODE_F2:
                if (actionUpEvent) { handleF2KeyPressed(); }
                return true;

            case KeyEvent.KEYCODE_F3:
                if (actionUpEvent) { handleF3KeyPressed(); }
                return true;
        }

        return super.dispatchKeyEvent(pEvent);

    }//end of StandardActivity::onKeyDown
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::onSaveInstanceState
    //
    // As the activity begins to stop, the system calls onSaveInstanceState()
    // so the activity can save state information with a collection of key-value
    // pairs. This functions is overridden so that additional state information can
    // be saved.
    //

    @Override
    public void onSaveInstanceState(Bundle pSavedInstanceState) {

        //store general values that is used by most (hopefully all) activities
        pSavedInstanceState.putParcelable(Keys.JOB_INFO_KEY, jobInfo);
        pSavedInstanceState.putParcelable(Keys.SHARED_SETTINGS_KEY, sharedSettings);

        storeActivitySpecificValuesToSavedInstance(pSavedInstanceState);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(pSavedInstanceState);

    }//end of StandardActivity::onSaveInstanceState
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::changeActivitySpecificBackgroundsForFocus
    //
    // Used by children classes to change the backgrounds of views depending on
    // the passed in view (focused view).
    //
    // We have to manually handle the changing of backgrounds because Android
    // has issues the state options ("state_focused", etc.) has issues when
    // it comes to focusing; it only works sometimes.
    //

    protected void changeActivitySpecificBackgroundsForFocus() {

    }//end of StandardActivity::changeActivitySpecificBackgroundsForFocus
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::createUiChangeListener
    //
    // Listens for visibility changes in the ui.
    //
    // If the system bars are visible, the system visibility is set to the uiOptions.
    //
    //

    private void createUiChangeListener() {

        decorView = getWindow().getDecorView();

        uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setOnSystemUiVisibilityChangeListener (
                new View.OnSystemUiVisibilityChangeListener() {

                    @Override
                    public void onSystemUiVisibilityChange(int pVisibility) {

                        if ((pVisibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            decorView.setSystemUiVisibility(uiOptions);
                        }

                    }

                });

    }//end of StandardActivity::createUiChangeListener
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::focusView
    //
    // Sets the focus of the activity to the passed in view.
    //

    protected void focusView(View pView) {

        if (viewInFocus != null) { viewInFocus.clearFocus(); }

        viewInFocus = pView;

        viewInFocus.requestFocus();

        changeActivitySpecificBackgroundsForFocus();

    }//end of StandardActivity::focusView
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::getJobInfoFromIntentExtras
    //
    // Extracts the JobInfo object from the intent extras.
    //
    // If a JobInfo object is not found in the extras, nothing is done.
    //

    private void getJobInfoFromIntentExtras() {

        if (getIntent().hasExtra(Keys.JOB_INFO_KEY)) {
            jobInfo = getIntent().getExtras().getParcelable(Keys.JOB_INFO_KEY);
        }

    }//end of StandardActivity::getJobInfoFromIntentExtras
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::getSharedSettingsFromIntentExtras
    //
    // Extracts the SharedSettings object from the intent extras.
    //
    // If a SharedSettings object is not found in the extras, one is instantiated.
    //

    private void getSharedSettingsFromIntentExtras() {

        if (getIntent().hasExtra(Keys.SHARED_SETTINGS_KEY)) {
            //DEBUG HSS//
            Log.d(LOG_TAG, "sharedSettings object contained in intent extras: YES");
            sharedSettings = getIntent().getExtras().getParcelable(Keys.SHARED_SETTINGS_KEY);
            sharedSettings.setContext(this);
        }
        else {
            Log.d(LOG_TAG, "sharedSettings object contained in intent extras: NO");
            sharedSettings = new SharedSettings(this);
            sharedSettings.init();
        }

    }//end of StandardActivity::getSharedSettingsFromIntentExtras
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::handleArrowDownKeyPressed
    //
    // Moves the focuses to the next view in the focus array.
    //

    private void handleArrowDownKeyPressed() {

        if (focusArray.isEmpty()) { return; }

        int index = focusArray.indexOf(viewInFocus);

        //if the focus is already on the last view, return
        if (index >= focusArray.size()-1) { return; }

        //if the view in focus was not found in the focus
        //array, then begin at the starting index
        //otherwise, go forward one in the array
        if (index == -1) { index = startingIndexOfFocusArray; }
        else { ++index; }

        focusView(focusArray.get(index));

    }//end of StandardActivity::handleArrowDownKeyPressed
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::handleArrowUpKeyPressed
    //
    // Moves the focuses to the previous view in the focus array.
    //

    private void handleArrowUpKeyPressed() {

        if (focusArray.isEmpty()) { return; }

        int index = focusArray.indexOf(viewInFocus);

        //if the focus is already on the first view, return
        if (index == 0) { return; }

        //if the view in focus was not found in the focus
        //array, then begin at the starting index
        //otherwise, go down one in the array
        if (index == -1) { index = startingIndexOfFocusArray; }
        else { --index; }

        focusView(focusArray.get(index));

    }//end of StandardActivity::handleArrowUpKeyPressed
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::handleEscapeKeyPressed
    //
    // Finishes the activity.
    //
    // Activities that were started for results will return RESULT_CANCELED.
    //
    // If the user should not be allowed to exit an activity by using the escape
    // button/key, this function should be overridden in that activity and left
    // blank.
    //

    protected void handleEscapeKeyPressed() {

        finish();

    }//end of StandardActivity::handleEscapeKeyPressed
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::handleF1KeyPressed
    //
    // Launches the QuickAction activity.
    //

    private void handleF1KeyPressed() {

        //WIP HSS// -- Need to add code

    }//end of StandardActivity::handleF1KeyPressed
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::handleF2KeyPressed
    //
    // Activity dependent -- children should override if necessary.
    //

    protected void handleF2KeyPressed() {

    }//end of StandardActivity::handleF2KeyPressed
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::handleF3KeyPressed
    //
    // Activity dependent -- children should override if necessary.
    //

    protected void handleF3KeyPressed() {

    }//end of StandardActivity::handleF3KeyPressed
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::performOnCreateActivitySpecificActions
    //
    // Children activities can override this function to perform different actions
    // depending on their individual requirements.
    //
    // Activity dependent -- children should override if necessary.
    //

    protected void performOnCreateActivitySpecificActions() {

    }//end of StandardActivity::performOnCreateActivitySpecificActions
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::performOnResumeActivitySpecificActions
    //
    // Children activities can override this function to perform different actions
    // depending on their individual requirements.
    //
    // Activity dependent -- children should override if necessary.
    //

    protected void performOnResumeActivitySpecificActions() {

    }//end of StandardActivity::performOnResumeActivitySpecificActions
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::storeActivitySpecificValuesFromSavedInstance
    //
    // Stores activity specific values in the passed in saved instance.
    //
    // Activity dependent -- children should override if necessary.
    //

    protected void storeActivitySpecificValuesToSavedInstance(Bundle pSavedInstanceState) {

    }//end of StandardActivity::storeActivitySpecificValuesFromSavedInstance
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::restoreActivitySpecificValuesFromSavedInstance
    //
    // Restores values using the passed in saved instance.
    //
    // Activity dependent -- children should override if necessary.
    //

    protected void restoreActivitySpecificValuesFromSavedInstance(Bundle pSavedInstanceState) {

    }//end of StandardActivity::restoreActivitySpecificValuesFromSavedInstance
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::restoreValuesFromSavedInstance
    //
    // Restores general values that are used by all activities using the passed
    // in saved instance.
    //

    private void restoreValuesFromSavedInstance(Bundle pSavedInstanceState) {

        if (pSavedInstanceState.containsKey(Keys.JOB_INFO_KEY)) {
            jobInfo = pSavedInstanceState.getParcelable(Keys.JOB_INFO_KEY);
        }

        if (pSavedInstanceState.containsKey(Keys.SHARED_SETTINGS_KEY)) {
            sharedSettings = pSavedInstanceState.getParcelable(Keys.SHARED_SETTINGS_KEY);
        }

    }//end of StandardActivity::restoreValuesFromSavedInstance
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::useActivitySpecificActivityStartUpValues
    //
    // Uses activity start up values for variables.
    //
    // Activity dependent -- children should override if necessary.
    //

    protected void useActivitySpecificActivityStartUpValues() {

    }//end of StandardActivity::useActivitySpecificActivityStartUpValues
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // StandardActivity::useActivityStartUpValues
    //
    // Uses activity start up values for general variables that are used by all
    // activities using the passed in saved instance.
    //

    private void useActivityStartUpValues() {

        getSharedSettingsFromIntentExtras();
        getJobInfoFromIntentExtras();

    }//end of StandardActivity::useActivityStartUpValues
    //-----------------------------------------------------------------------------

}//end of class StandardActivity
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
