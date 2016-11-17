package com.cxp.cyclexpro_v2;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import static com.cxp.cyclexpro_v2.TitleBarActivity.updateConBtn;

/**
 * This is a JAVA class that defines the Handler.
 * The Handler handles messages between the Activities
 */
public class CustomHandler extends Handler {
    Context con;
    public CustomHandler(Context context) {
        con = context;
        //In case we need to pass objects to the Handler
    }

    /** Parses the Handler message */
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch (msg.what){
            case Constants.SUCCESS_CONNECT:
                if(BluetoothActivity.sConnectedThread != null){
                    Log.i("Check", "Already Connected");
                } else {
                    Log.i("Check", "SUCCESS_CONNECT");
                    BluetoothActivity.sConnectedThread =
                            new BluetoothActivity.ConnectedThread(
                                    (BluetoothSocket) msg.obj);
                    Toast.makeText(con, "Connected!",
                            Toast.LENGTH_SHORT).show();
                    BluetoothActivity.sConnectedThread.start();
                }
                break;
            case Constants.IDLE_READ:
                byte[] idleBuf = (byte[]) msg.obj;

                //int blvl = idleBuf[msg.arg1];
                Log.d("threat", Integer.toString((int) idleBuf[msg.arg1+1]));
                TitleBarActivity.updateBatteryLvl((int) idleBuf[msg.arg1]);
                TitleBarActivity.updateThreatIndicator((int) idleBuf[msg.arg1+1]);
                //Log.i("BLVL", Integer.toString(blvl));
                break;
            case Constants.DATA_READ:
                byte[] dataBuf = (byte[]) msg.obj;
                //msg.arg1 is battery
                //msg.arg2-1 is LF character, want
                //int blvl2 = dataBuf[msg.arg1];
                //battery
                TitleBarActivity.updateBatteryLvl((int) dataBuf[msg.arg1]);
                TitleBarActivity.updateThreatIndicator((int) dataBuf[msg.arg1+1]);
                //Log.i("BLVL", Integer.toString(blvl2));
                Log.i("Check", "parseData");
                MetricsActivity.parseData( Arrays.copyOfRange(dataBuf, msg.arg1+2, msg.arg2));
                break;
            case Constants.HEADER_READ:
                byte[] headerBuf = (byte[]) msg.obj;
                Log.i("Check", "parseHead");
                if(Globals.sMode == Constants.MODE_RACE){
                    RaceActivity.parseHeader(Arrays.copyOfRange(headerBuf, msg.arg1, msg.arg2));
                } else {
                    MetricsActivity.parseHeader( Arrays.copyOfRange(headerBuf, msg.arg1, msg.arg2));
                }
                break;
            case Constants.ERPS_READ:
                byte[] erpsBuf = (byte[]) msg.obj;
                Log.i("Check", "parseERPS");
                if(Globals.sMode == Constants.MODE_RACE){
                    RaceActivity.launchERPS(Arrays.copyOfRange(erpsBuf, msg.arg1, msg.arg2));
                } else {
                    MetricsActivity.launchERPS( Arrays.copyOfRange(erpsBuf, msg.arg1, msg.arg2));
                }
                Log.d("DEB","launching ERPS" );
                break;
            case Constants.RACE_READ:
                byte[] raceBuf = (byte[]) msg.obj;
                //msg.arg1 is battery
                //msg.arg2 is protocol, don't want
                //msg.arg2-1 is LF character, want
                Log.i("Check", "parseRace");
                TitleBarActivity.updateBatteryLvl((int) raceBuf[msg.arg1]);
                TitleBarActivity.updateThreatIndicator((int) raceBuf[msg.arg1+1]);
                RaceActivity.parseData(Arrays.copyOfRange(raceBuf, msg.arg1+2, msg.arg2));
                break;
            case Constants.XB_CONNECT:
                byte[] xbBuf = (byte[]) msg.obj;
                Log.d("threat", Integer.toString((int) xbBuf[msg.arg1+1]));
                TitleBarActivity.updateBatteryLvl((int) xbBuf[msg.arg1]);
                TitleBarActivity.updateThreatIndicator((int) xbBuf[msg.arg1+1]);
                Globals.sMyTRIOid = new String(Arrays.copyOfRange(xbBuf, msg.arg1+2, msg.arg2));
                Log.d("xb", Globals.sMyTRIOid);
                Globals.editor.putString("my_trio_id", Globals.sMyTRIOid);
                Globals.editor.apply();

                break;
            default:
                Log.i("msg.what", Integer.toString(msg.what));
        }
    }
}