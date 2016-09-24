package com.cxp.cyclexpro_v2;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

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
                int blvl = idleBuf[msg.arg1];
                Log.i("BLVL", Integer.toString(blvl));
                break;
            case Constants.DATA_READ:
                byte[] dataBuf = (byte[]) msg.obj;
                Log.i("Check", "parseData");
                MetricsActivity.parseData( Arrays.copyOfRange(dataBuf, msg.arg1, msg.arg2));
                break;
            case Constants.HEADER_READ:
                byte[] headerBuf = (byte[]) msg.obj;
                Log.i("Check", "parseHead");
                MetricsActivity.parseHeader( Arrays.copyOfRange(headerBuf, msg.arg1, msg.arg2));
                break;
            case Constants.ERPS_READ:
                byte[] erpsBuf = (byte[]) msg.obj;
                Log.i("Check", "parseERPS");
                BluetoothActivity.sConnectedThread.write(Constants.ERPS_ACK);
                MetricsActivity.launchERPS( Arrays.copyOfRange(erpsBuf, msg.arg1, msg.arg2));
                break;
            case Constants.XB_CONNECT:
                Globals.sXbConnected = true;
            default:
                Log.i("msg.what", Integer.toString(msg.what));
        }
    }
}