package com.cxp.cyclexpro_v2;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by Carlos on 6/29/2016.
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
                Log.i("Check", "SUCCESS_CONNECT");
                BluetoothActivity.sConnectedThread =
                        new BluetoothActivity.ConnectedThread(
                                (BluetoothSocket) msg.obj);
                Toast.makeText(con, "Connected!",
                        Toast.LENGTH_SHORT).show();
                BluetoothActivity.sConnectedThread.start();
                break;
            case Constants.MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                MetricsActivity.parseData( Arrays.copyOfRange(readBuf, msg.arg1, msg.arg2));
                break;
            case Constants.XB_CONNECT:
                Globals.sXbConnected = true;
            default:
                Log.i("Check", "Default Case");
        }
    }
}