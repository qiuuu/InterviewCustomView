package com.pangge.interviewcustomview.view;

import android.content.Context;

/**
 * Created by iuuu on 17/10/13.
 */

public interface CustomTextView {
    //Context getContext();
   // CharSequence getText();
    String getSelectedText();

    void onTextSelected();
    void onTextUnselected();

}






