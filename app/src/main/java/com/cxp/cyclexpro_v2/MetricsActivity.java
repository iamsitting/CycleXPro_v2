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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * This activity presents data
 * Uses GraphView to plot data
 */
public class MetricsActivity extends TitleBarActivity implements View.OnClickListener{

    ToggleButton tbSession;
    Button btTest;
    static TextView tvMetric0, tvMetric1, tvMetric2, tvMetric3;
    static TextView tvLabel0, tvLabel1, tvLabel2, tvLabel3;
    private static Context sContext;

    //Declare some variables
    static boolean autoScrollX;
    private static double graph2LastXValue = 5d;
    private static int maxPoints = 40;

    private static LineGraphSeries<DataPoint> mSeries;

    static DataLogger dl;
    String lastFileEdited;
    String savedDate;
    int savedSession;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metrics);
        sContext = getApplicationContext();
        init();
        ButtonInit();
    }

    /** initializes graphView object */
    void init(){
        Bundle extras = getIntent().getExtras();
        String mode = extras.getString("Mode");

        switch (mode){
            case "SOLO":
                this.tvTitle.setText("Your Metrics");
                Globals.sMode = Constants.MODE_SOLO;
                break;
            case "TRAINER":
                this.tvTitle.setText("Cyclist's Metrics");
                Globals.sMode = Constants.MODE_TRAINER;
                break;
            case "TRAINEE":
                this.tvTitle.setText("Your Metrics");
                Globals.sMode = Constants.MODE_TRAINEE;
                break;
        }

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

        //File naming
        savedDate = Globals.memory.getString(Constants.PREFS_KEY_DATE,
                Constants.DATE_NOT_EXISTS);
        savedSession = Globals.memory.getInt(Constants.PREFS_KEY_SESSION,
                Constants.SESH_NOT_EXISTS);
    }

    /** initializes View objects */
    void ButtonInit(){
        btBtConnection.setOnClickListener(this);
        tvLabel0 = (TextView) findViewById(R.id.tvLabel0);
        tvLabel1 = (TextView) findViewById(R.id.tvLabel1);
        tvLabel2 = (TextView) findViewById(R.id.tvLabel2);
        tvLabel3 = (TextView) findViewById(R.id.tvLabel3);
        tvMetric0 = (TextView) findViewById(R.id.tvSpeed);
        tvMetric1 = (TextView) findViewById(R.id.tvMetric1);
        tvMetric2 = (TextView) findViewById(R.id.tvMetric2);
        tvMetric3 = (TextView) findViewById(R.id.tvMetric3);
        btTest = (Button) findViewById(R.id.btTest);
        btTest.setOnClickListener(this);
        tbSession = (ToggleButton) findViewById(R.id.tbSession);
        tbSession.setOnClickListener(this);
    }

    /** Listens for button clicks and responds accordingly */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btTest:
                Intent intent = new Intent(MetricsActivity.this, ERPSActivity.class);
                intent.putExtra("erpsData", new byte[8]);
                intent.putExtra("currentTime", "MM/DD/YY");
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sContext.startActivity(intent);
                break;
            case R.id.tbSession:
                if(tbSession.isChecked()){
                    if(BluetoothActivity.sConnectedThread != null){
                        Globals.sGoodHeaderRead = false;
                        Globals.sSessionOn = true;
                        BluetoothActivity.sConnectedThread.write((Constants.SOLO_SESSION));
                    }
                    String name = makeFileName(savedDate, savedSession);
                    dl = new DataLogger(name, this);
                    dl.start();
                    dl.startWriting();
                } else {
                    if(BluetoothActivity.sConnectedThread != null){
                        BluetoothActivity.sConnectedThread.write(Constants.END_SESSION);
                    }
                    dl.stopWriting();
                    while (dl.isAlive()) {//TODO: Add timeout
                        lastFileEdited = dl.getFileName();
                        dl.finishLog();
                    }
                    Globals.sSessionOn = false;

                    new AlertDialog.Builder(this)
                            .setTitle("Save Session")
                            .setMessage("Do you want to save this session?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    Globals.editor.putInt(Constants.PREFS_KEY_SESSION, ++savedSession);
                                    Globals.editor.apply();
                                    Toast.makeText(getApplicationContext(), lastFileEdited+" saved!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    File delFile = new File(getFilesDir().getAbsolutePath(), lastFileEdited);
                                    boolean delCheck = delFile.delete();
                                    if(delCheck){
                                        Toast.makeText(getApplicationContext(), lastFileEdited+" deleted!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                    })
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
        //Globals.sBuffer = byteArray;
        //Globals.sNewData = true;
        String toParse = "";
        //time
        int hour = byteArray[0];
        int minute = byteArray[1];
        float second = ByteBuffer.wrap(byteArray, 2, 4)
                .order(ByteOrder.BIG_ENDIAN).getFloat();
        String setText0 = Integer.toString(hour) + ":" +
                Integer.toString(minute) + ":" +
                String.format("%.2f", second);
        //metric1
        float met1 = ByteBuffer.wrap(byteArray, 6,4)
                .order(ByteOrder.BIG_ENDIAN).getFloat();
        String setText1 = String.format("%.1f", met1);
        //met2
        float met2 = ByteBuffer.wrap(byteArray, 10, 4)
                .order(ByteOrder.BIG_ENDIAN).getFloat();
        String setText2 = String.format("%.1f", met2);
        //met3
        float met3 = ByteBuffer.wrap(byteArray, 14, 4)
                .order(ByteOrder.BIG_ENDIAN).getFloat();
        String setText3 = String.format("%.1f", met3);

        toParse = setText0+","+setText1+","+setText2+
                ","+setText3;
        Globals.sBuffer = (toParse+"\n").getBytes(StandardCharsets.UTF_8);
        Log.i("pData", toParse);
        Globals.sNewData = true;

        tvMetric0.setText(setText0);
        tvMetric1.setText(setText1);
        tvMetric2.setText(setText2);
        tvMetric3.setText(setText3);

        plotData(met1);

        BluetoothActivity.sConnectedThread.write(Constants.SEND_NEXT_SAMPLE);

    }

    public static void parseHeader(byte[] byteArray){
        Globals.sBuffer = byteArray;
        Globals.sNewData = true;
        String byteToString = "";
        Log.d("H_array", Globals.getHexString(byteArray, byteArray.length));
        try{
            //removes the 0x0A (NL) character
            byteToString = new String(byteArray, 0, byteArray.length-1, "UTF-8");
            Log.i("checkData", byteToString);
        } catch (UnsupportedEncodingException  e){
            Log.e("StrX", e.toString());
        }

        String[] dataArray = byteToString.split(",");
        switch (dataArray.length){
            case 4:
                tvLabel3.setText(dataArray[3]);
            case 3:
                tvLabel2.setText(dataArray[2]);
            case 2:
                tvLabel1.setText(dataArray[1]);
            case 1:
                tvLabel0.setText(dataArray[0]);
                break;
            default:

        }

        BluetoothActivity.sConnectedThread.write(Constants.SEND_NEXT_SAMPLE);
    }

    public static void launchERPS(byte[] byteArray){
        Log.d("DEB","launching ERPS" );
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

        //do if dl.exists()
        dl.stopWriting();
        while (dl.isAlive()) {//TODO: Add timeout
            dl.finishLog();
        }
        Intent intent = new Intent(sContext, ERPSActivity.class);
        intent.putExtra("erpsData", byteArray);
        intent.putExtra("currentTime", nowAsISO);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sContext.startActivity(intent);
    }


    public static void plotData(float value){
        Log.i("plotData", Float.toString(value));
        mSeries.appendData(new DataPoint(graph2LastXValue, value), autoScrollX, maxPoints);
        graph2LastXValue += 1d;
        Log.i("plotData", "Plotted");
    }

    public String makeFileName(String date, int session){
        return date+"-"+Integer.toString(session)+".csv";
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
