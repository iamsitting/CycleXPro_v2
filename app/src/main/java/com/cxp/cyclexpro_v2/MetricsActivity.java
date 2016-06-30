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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * This activity presents data
 * Uses GraphView to plot data
 */
public class MetricsActivity extends TitleBarActivity implements View.OnClickListener{

    ToggleButton tbStream, tbSession;
    static TextView tvSpeed, tvMetric1, tvMetric2, tvMetric3;

    //Declare some variables
    static boolean autoScrollX;
    private static double graph2LastXValue = 5d;
    private static int maxPoints = 40;

    private static LineGraphSeries<DataPoint> mSeries;

    DataLogger dl;
    String lastFileEdited;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metrics);
        init();
        ButtonInit();

    }

    /** initializes graphView object */
    void init(){
        this.tvTitle.setText("Your Metrics");
        this.tvTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        mSeries = new LineGraphSeries<>();
        graph.addSeries(mSeries);

        //X-Axis
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(5);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);

        //conditions
        autoScrollX = true;
        Globals.sNewData = false;
        Globals.sDataString = "";

    }

    /** initializes View objects */
    void ButtonInit(){
        btBtConnection.setOnClickListener(this);
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        tvMetric1 = (TextView) findViewById(R.id.tvMetric1);
        tvMetric2 = (TextView) findViewById(R.id.tvMetric2);
        tvMetric3 = (TextView) findViewById(R.id.tvMetric3);
        tbStream = (ToggleButton) findViewById(R.id.tbStream);
        tbStream.setOnClickListener(this);
        tbStream.setEnabled(false);
        tbSession = (ToggleButton) findViewById(R.id.tbSession);
        tbSession.setOnClickListener(this);
    }

    /** Listens for button clicks and responds accordingly */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tbStream:
                if(tbStream.isChecked()){
                    if(BluetoothActivity.sConnectedThread != null){
                        BluetoothActivity.sConnectedThread.write(Constants.START_STREAM);
                        dl.startWriting();
                    }
                } else {
                    if(BluetoothActivity.sConnectedThread != null){
                        BluetoothActivity.sConnectedThread.write(Constants.STOP_STREAM);
                        dl.stopWriting();
                    }
                    tbSession.setEnabled(true);
                }
                break;
            case R.id.tbSession:
                if(tbSession.isChecked()){ //TODO: Automate file naming
                    dl = new DataLogger("todayDate.csv", this);
                    dl.start();
                    tbStream.setEnabled(true);
                    tbSession.setEnabled(false);
                } else {
                    tbStream.setEnabled(false);
                    while (dl.isAlive()) {//TODO: Add timeout
                        lastFileEdited = dl.getFileName();
                        dl.finishLog();
                    }

                    new AlertDialog.Builder(this)
                            .setTitle("Save Session")
                            .setMessage("Do you want to save this session?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    Toast.makeText(getApplicationContext(), lastFileEdited+" saved!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }).setNegativeButton("No", null)//TODO: Change negative effect
                            .show();
                    //if user wants data to get pushed:

                }
                break;
            case R.id.conbtn:
                if(!Globals.sBtConnected){
                    startActivity(new Intent(MetricsActivity.this, BluetoothActivity.class));
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
                }
                btBtConnection.invalidate();
                break;
            default:
        }
    }

    /**
     * Parses the incoming Bluetooth data from byte array to string
     * @param byteArray       data from the Bluetooth device
     */
    public static void parseData(byte[] byteArray){

        Globals.sBuffer = byteArray;
        Globals.sNewData = true;
        String[] dataArray = new String(byteArray).split(",");

        Log.i("Check0", dataArray[0]);
        //only plot the first metric
        tvSpeed.setText(dataArray[0]);
        plotData(dataArray[0]);
        tvMetric1.setText(dataArray[1]);
        tvMetric2.setText(dataArray[2]);
        tvMetric3.setText(dataArray[3]);
        BluetoothActivity.sConnectedThread.write(Constants.SEND_NEXT_SAMPLE);
    }

    /**
     * Updates GraphView each time its updated
     * @param strIncom      the incoming message string
     */
    public static void plotData(String strIncom){
        if(strIncom.indexOf('s')==0 && strIncom.indexOf('.')==2){
            strIncom = strIncom.replace("s", "");
            if(isFloatNumber(strIncom)){
                //tvSpeed.setText(strIncom);
                mSeries.appendData(new DataPoint(graph2LastXValue,
                                Double.parseDouble(strIncom)),
                        autoScrollX, maxPoints);

                graph2LastXValue += 1d;

            }
        }
    }

    /**
     * Returns true if string can be converted to float
     * @param num       a string that may be a float
     * @return          boolean: true if string of a flaot
     */
    public static boolean isFloatNumber(String num){
        try{
            Double.parseDouble(num);
        } catch (NumberFormatException nfe){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
