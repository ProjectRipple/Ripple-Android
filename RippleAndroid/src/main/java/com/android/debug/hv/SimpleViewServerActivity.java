package com.android.debug.hv;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Class for usage with the ViewServer for viewing the layout hierarchy.
 * To use, have the activity you wish to check extend this class.
 * DO NOT extend this class when done debugging the hierarchy as it is a security issue.
 * Created by james on 7/15/14.
 */
public class SimpleViewServerActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewServer.get(this).addWindow(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
    }
}
