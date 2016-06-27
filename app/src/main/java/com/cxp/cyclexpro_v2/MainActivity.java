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

import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * App launches into this activity
 * Presents mode of operations
 */
public class MainActivity extends TitleBarActivity implements View.OnClickListener{
    public static CustomHandler sHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    /** initializes the Views*/
    void init(){

        this.tvTitle.setText("Cycle-X Pro");
        btBtConnection.setOnClickListener(this);
        Button solobtn = (Button) findViewById(R.id.solobtn);
        solobtn.setOnClickListener(this);
        Button trainbtn = (Button) findViewById(R.id.trainbtn);
        trainbtn.setOnClickListener(this);
        Button racebtn = (Button) findViewById(R.id.racebtn);
        racebtn.setOnClickListener(this);
        sHandler = new CustomHandler();
    }

    /**
     * Listens to a button click
     * Launches appropriate activity
     * @param v     an Android View (Button)
     */
    @Override
    public void onClick(View v){
        Class cl;
        switch(v.getId()){
            case R.id.solobtn:
                cl = MetricsActivity.class;
                startActivity(new Intent(this, cl));
                break;
            case R.id.trainbtn:
                cl = TrainingActivity.class;
                startActivity(new Intent(this, cl));
                break;
            case R.id.racebtn:
                cl = MetricsActivity.class;
                startActivity(new Intent(this, cl));
                break;
            case R.id.conbtn:
                if(!sBtConnected){
                    cl = BluetoothActivity.class;
                    startActivity(new Intent(this, cl));
                    break;
                } else{

                    new AlertDialog.Builder(this)
                            .setTitle("Disconnecting Bluetooth")
                            .setMessage("Are you sure you want to disconnect?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    BluetoothActivity.disconnect();
                                }
                            }).setNegativeButton("No", null)
                            .show();
                    break;
                }
            default:
                Log.i("Check", "def...");
        }
    }

    /**
     * A custom interface for handler
     */
    public class CustomHandler extends Handler {

        public CustomHandler() {
            //In case we need to pass objects to the Handler
        }

        /** Parses the Handler message */
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case Constants.SUCCESS_CONNECT:
                    Log.i("Check", "SUCCESS_CONNECT");
                    BluetoothActivity.sConnectedThread =
                            new BluetoothActivity.ConnectedThread(
                                    (BluetoothSocket) msg.obj);
                    Toast.makeText(getApplicationContext(), "Connected!",
                            Toast.LENGTH_SHORT).show();
                    BluetoothActivity.sConnectedThread.start();
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    MetricsActivity.parseData(readBuf);
                    break;
                case Constants.XB_CONNECT:
                    updateXBConnectionStatus(true);
                default:
                    Log.i("Check", "Default Case");
            }
        }
    }


}
