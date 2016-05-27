/*
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
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.lang.ref.WeakReference;

/**
 * This activity presents data
 * Uses GraphView to plot data
 */
public class MetricsActivity extends TitleBarActivity implements View.OnClickListener{
    //public static CustomHandler sHandler;
    public static Handler mHandler;

    static final LineGraphSeries<DataPoint> mSeries =
            new LineGraphSeries<>(new DataPoint[]{});

    ToggleButton tbStream;

    //Declare some variables
    static boolean AutoScrollX, Lock;


    private static double graph2LastXValue = 0;
    private static int Xview = 10;
    private static int maxPoints = 40;



/*
    protected static Handler mHandler = new Handler(){

        */
/*

        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Log.i("msg.what", Integer.toString(msg.what));
            switch (msg.what){
                case Constants.SUCCESS_CONNECT:
                    Log.i("Check", "SUCCESS_CONNECT");
                    BluetoothActivity.sConnectedThread =
                            new BluetoothActivity.ConnectedThread((BluetoothSocket)msg.obj);
                    Toast.makeText(getApplicationContext,
                            "Connected!", Toast.LENGTH_SHORT).show();
                    Log.i("Check", "TOASTED");
                    BluetoothActivity.sConnectedThread.start();
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String strIncom = new String(readBuf, 0, 5);

                    if(strIncom.indexOf('s')==0 && strIncom.indexOf('.')==2){
                        strIncom = strIncom.replace("s", "");
                        if(isFloatNumber(strIncom)){
                            mSeries.appendData(new DataPoint(graph2LastXValue,
                                    Double.parseDouble(strIncom)), AutoScrollX, maxPoints);
                            if (graph2LastXValue >= Xview && Lock == true){
                                mSeries.resetData(new DataPoint[] {});
                                graph2LastXValue = 0;
                            } else {
                                graph2LastXValue += 1;
                            }

                        }
                    }
                    break;
                default:
                    Log.i("check", "Default case");
                    Log.i("msg.what", Integer.toString(msg.what));
            }
        }



        public boolean isFloatNumber(String num){
            try{
                Double.parseDouble(num);
            } catch (NumberFormatException nfe){
               return false;
            }
            return true;
        }
    };
*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metrics);
        init();
        Buttoninit();
    }

    /** initializes graphView object */
    void init(){
        final GraphView graph = (GraphView) findViewById(R.id.graph);
        final LineGraphSeries<DataPoint> mSeries = new LineGraphSeries<>(
                new DataPoint[]{});

        //X-Axis
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(5);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.addSeries(mSeries);

        //sHandler = new CustomHandler();

        mHandler = new Handler() {

            /**
             * handles and parses message content
             *
             * @param msg Message passed via handler
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.i("msg.what", Integer.toString(msg.what));
                switch (msg.what) {
                    case Constants.SUCCESS_CONNECT:
                        Log.i("Check", "SUCCESS_CONNECT");
                        BluetoothActivity.sConnectedThread =
                                new BluetoothActivity.ConnectedThread((BluetoothSocket) msg.obj);
                        Toast.makeText(MetricsActivity.this,
                                "Connected!", Toast.LENGTH_SHORT).show();
                        Log.i("Check", "TOASTED");
                        BluetoothActivity.sConnectedThread.start();
                        break;
                    case Constants.MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, 5);

                        if (strIncom.indexOf('s') == 0 && strIncom.indexOf('.') == 2) {
                            strIncom = strIncom.replace("s", "");
                            if (isFloatNumber(strIncom)) {
                                mSeries.appendData(new DataPoint(graph2LastXValue,
                                        Double.parseDouble(strIncom)), AutoScrollX, maxPoints);
                                if (graph2LastXValue >= Xview && Lock == true) {
                                    mSeries.resetData(new DataPoint[]{});
                                    graph2LastXValue = 0;
                                } else {
                                    graph2LastXValue += 1;
                                }

                            }
                        }
                        break;
                    default:
                        Log.i("check", "Default case");
                        Log.i("msg.what", Integer.toString(msg.what));
                }
            }

            /**
             * Returns true if string can be converted to float
             *
             * @param num a string that may be a float
             * @return boolean: true if string of a flaot
             */
            public boolean isFloatNumber(String num) {
                try {
                    Double.parseDouble(num);
                } catch (NumberFormatException nfe) {
                    return false;
                }
                return true;
            }
        };

    }

    /** initializes View objects */
    void Buttoninit(){
        conbtn.setVisibility(View.GONE);
        tbStream = (ToggleButton) findViewById(R.id.tbStream);
    }

    /** Listens for button clicks and responds accordingly */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tbStream:
                if(tbStream.isChecked()){
                    if(BluetoothActivity.sConnectedThread != null){
                        BluetoothActivity.sConnectedThread.write(Constants.START_STREAM);
                    }
                } else {
                    if(BluetoothActivity.sConnectedThread != null){
                        BluetoothActivity.sConnectedThread.write(Constants.STOP_STREAM);
                    }
                }
                break;
            default:
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

    public static void plotData(String strIncom){
        if(strIncom.indexOf('s')==0 && strIncom.indexOf('.')==2){
            strIncom = strIncom.replace("s", "");
            if(isFloatNumber(strIncom)){
                mSeries.appendData(new DataPoint(graph2LastXValue,
                                Double.parseDouble(strIncom)),
                        AutoScrollX, maxPoints);
                if(graph2LastXValue >= Xview && Lock == true){
                    mSeries.resetData(new DataPoint[]{});
                    graph2LastXValue = 0;
                } else {
                    graph2LastXValue += 1;
                }
            }
        }
    }

}
