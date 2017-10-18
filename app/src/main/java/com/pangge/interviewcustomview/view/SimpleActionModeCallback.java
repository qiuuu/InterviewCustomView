package com.pangge.interviewcustomview.view;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by iuuu on 17/10/13.
 */

public class SimpleActionModeCallback implements ActionMode.Callback {
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
       // mode.setCustomView();
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
}
