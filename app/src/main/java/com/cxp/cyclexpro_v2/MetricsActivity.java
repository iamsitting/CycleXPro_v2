package com.cxp.cyclexpro_v2;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
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

public class MetricsActivity extends TitleBarActivity {
    public static Handler mHandler;


    ToggleButton tbStream;


    //Declare some variables
    static boolean AutoScrollX, Lock;


    private static double graph2LastXValue = 0;
    private static int Xview = 10;
    private static int maxPoints = 40;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metrics);
        init();
    }

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

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                Log.i("msg.what", Integer.toString(msg.what));
                switch (msg.what){
                    case BluetoothActivity.SUCCESS_CONNECT:
                        Log.i("Check", "SUCCESS_CONNECT");
                        BluetoothActivity.connectedThread =
                                new BluetoothActivity.ConnectedThread((BluetoothSocket)msg.obj);
                        Toast.makeText(getApplicationContext(),
                                "Connected!", Toast.LENGTH_SHORT).show();
                        Log.i("Check", "TOASTED");
                        BluetoothActivity.connectedThread.start();
                        break;
                    case BluetoothActivity.MESSAGE_READ:
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
    }

    void Buttoninit(){
        conbtn.setVisibility(View.GONE);
        tbStream = (ToggleButton) findViewById(R.id.tbStream);
    }
}
