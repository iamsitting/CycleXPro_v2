package com.cxp.cyclexpro_v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RaceActivity extends TitleBarActivity implements View.OnClickListener {
    ToggleButton tbSession;
    Button btTest;
    static TextView tvMetric0, tvMetric1, tvMetric2, tvMetric3, tvPlace;
    static TextView tvLabel0, tvLabel1, tvLabel2, tvLabel3;
    private static Context sContext;
    protected static int place = 1;

    //Declare some variables
    static boolean autoScrollX;
    private static double graph2LastXValue = 5d;
    private static int maxPoints = 40;

    private static LineGraphSeries<DataPoint> mSeries;
    private static LineGraphSeries<DataPoint> mOppSeries;

    static DataLogger dl;
    String lastFileEdited;
    String savedDate;
    int savedSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);
        sContext = getApplicationContext();
        init();
        ButtonInit();
    }
    /** initializes graphView object */
    void init(){
        Globals.sMode = Constants.MODE_RACE;
        this.tvTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        mSeries = new LineGraphSeries<>();
        mSeries.setColor(Color.GREEN);
        mOppSeries = new LineGraphSeries<>();
        mOppSeries.setColor(Color.RED);
        graph.addSeries(mSeries);
        graph.addSeries(mOppSeries);

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
        tvPlace = (TextView) findViewById(R.id.tvPlace);
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
                Intent intent = new Intent(RaceActivity.this, ERPSActivity.class);
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
                        BluetoothActivity.sConnectedThread.write((Constants.RACE_SESSION));
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
                    startActivity(new Intent(RaceActivity.this, BluetoothActivity.class));
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
        float speed = ByteBuffer.wrap(byteArray, 6,4)
                .order(ByteOrder.BIG_ENDIAN).getFloat();
        String setText1 = String.format("%.1f", speed);

        //met2
        float distance = ByteBuffer.wrap(byteArray, 10, 4)
                .order(ByteOrder.BIG_ENDIAN).getFloat();
        String setText2 = String.format("%.1f", distance);

        //met3
        float calories = ByteBuffer.wrap(byteArray, 14, 4)
                .order(ByteOrder.BIG_ENDIAN).getFloat();
        String setText3 = String.format("%.1f", calories);

        //met4
        float oppSpeed = ByteBuffer.wrap(byteArray, 18, 4)
                .order(ByteOrder.BIG_ENDIAN).getFloat();
        String setText4 = String.format("%.1f", oppSpeed);

        //met5
        float oppDistance = ByteBuffer.wrap(byteArray, 22, 4)
                .order(ByteOrder.BIG_ENDIAN).getFloat();
        String setText5 = String.format("%.1f", oppDistance);

        toParse = setText0+","+setText1+","+setText2+
                ","+setText3;
        updatePlace(byteArray[26]);
        Globals.sBuffer = (toParse+"\n").getBytes(StandardCharsets.UTF_8);
        Log.i("pData", toParse);
        Globals.sNewData = true;

        tvMetric0.setText(setText0);
        tvMetric1.setText(setText1);
        tvMetric2.setText(setText4);
        tvMetric3.setText(setText5);

        plotData(speed, oppSpeed);
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
        } catch (UnsupportedEncodingException e){
            Log.e("StrX", e.toString());
        }

        BluetoothActivity.sConnectedThread.write(Constants.SEND_NEXT_SAMPLE);
    }

    public static void launchERPS(byte[] byteArray){
        Log.d("DEB","launching ERPS" );
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

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


    public static void plotData(float value1, float value2){
        Log.i("plotData", Float.toString(value1));
        mSeries.appendData(new DataPoint(graph2LastXValue, value1), autoScrollX, maxPoints);
        mOppSeries.appendData(new DataPoint(graph2LastXValue, value2), autoScrollX, maxPoints);
        graph2LastXValue += 1d;
    }

    public String makeFileName(String date, int session){
        return date+"-"+Integer.toString(session)+".csv";
    }

    public static void updatePlace(int val){
        final int x = val;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if(x == 1){
                    if(place == 2){
                        place = 1;
                        tvPlace.setText(Integer.toString(place));
                    }
                } else {
                    if(place == 1){
                        place = 2;
                        tvPlace.setText(Integer.toString(place));
                    }
                }
            }
        };
        runOnUI(r);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
