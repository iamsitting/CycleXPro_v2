/*
 * This file is licensed under MIT
 *
 * The MIT License (MIT)
 *
 * Copyright (C) 2016 Carlos Salamanca (@iamsitting)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy modify, merge publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTIBILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

/*
 * @author Carlos Salamanca
 * @version 2.0.0
 */
package com.cxp.cyclexpro_v2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

/**
 * TitleBarActivity is a superclass; used with extends
 * Creates a custom title bar for the app.
 */
public class TitleBarActivity extends Activity {
    protected static Button btBtConnection;
    protected TextView tvTitle;
    protected Button btMenu;

    //TODO: Add battery indicator
    /** creates title bar with button and textView */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.activity_title_bar);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);

        btBtConnection = (Button) findViewById(R.id.conbtn);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        updateConBtn();

        btMenu = (Button) findViewById(R.id.btMenu);
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateConBtn();
    }

    /*
    public static void updateConnectionStatus(boolean status){
        sBtConnected = status;
        updateConBtn();
    }*/

    public static void updateConBtn(){
        Log.i("Check", String.valueOf(Globals.sBtConnected));
        if (Globals.sBtConnected){
            btBtConnection.setBackgroundResource(R.drawable.ic_bluetooth_connect_white_36dp);

        }
        else{
            btBtConnection.setBackgroundResource(R.drawable.ic_bluetooth_off_grey600_36dp);

        }
        btBtConnection.invalidate();
    }



}
